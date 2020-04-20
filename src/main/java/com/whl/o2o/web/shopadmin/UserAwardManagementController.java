package com.whl.o2o.web.shopadmin;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.whl.o2o.dto.ShopAuthMapExecution;
import com.whl.o2o.dto.UserAccessToken;
import com.whl.o2o.dto.UserAwardMapExecution;
import com.whl.o2o.dto.WechatInfo;
import com.whl.o2o.entity.*;
import com.whl.o2o.enums.UserAwardMapStateEnum;
import com.whl.o2o.enums.UserInfoStateEnum;
import com.whl.o2o.exceptions.UserAwardMapOperationException;
import com.whl.o2o.service.ShopAuthMapService;
import com.whl.o2o.service.UserAwardMapService;
import com.whl.o2o.service.UserInfoService;
import com.whl.o2o.service.WeChatAuthService;
import com.whl.o2o.util.HttpServletRequestUtil;
import com.whl.o2o.util.weixin.WechatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/shopadmin")
public class UserAwardManagementController {
	@Autowired
	private UserAwardMapService userAwardMapService;
	@Autowired
	private UserInfoService personInfoService;
	@Autowired
	private ShopAuthMapService shopAuthMapService;
	@Autowired
	private WeChatAuthService wechatAuthService;

	/**
	 * 列出某个店铺的用户奖品领取情况列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listuserawardmapsbyshop", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listUserAwardMapsByShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<>();
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		if (pageIndex <= 0 || pageSize <= 0) {
			throw new UserAwardMapOperationException("非法的pageIndex或pageSize");
		}
		if (currentShop == null || currentShop.getShopId() == null) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "当前店铺信息获取失败");
			return modelMap;
		}
		UserAwardMap userAwardMap = new UserAwardMap();
		userAwardMap.setShop(currentShop);

		String awardName = HttpServletRequestUtil.getString(request, "awardName");// 从请求中获取奖品名
		if (awardName != null) {
			// 如果需要按照奖品名称搜索，则添加搜索条件
			Award award = new Award();
			award.setAwardName(awardName);
			userAwardMap.setAward(award);
		}
		// 分页返回结果
		UserAwardMapExecution ue = userAwardMapService.listReceivedUserAwardMap(userAwardMap, pageIndex, pageSize);
		modelMap.put("userAwardMapList", ue.getUserAwardMapList());
		modelMap.put("count", ue.getCount());
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 操作员扫顾客的奖品二维码并派发奖品，证明顾客已领取过
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/exchangeaward", method = RequestMethod.GET)
	private String exchangeAward(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取负责扫描二维码的店员信息
		WeChatAuth auth = getOperatorInfo(request);
		if (auth == null) {
			return null;
		}
		// 通过userId获取店员信息
		UserInfo operator = personInfoService.getUserInfoById(auth.getUserInfo().getUserId()).getPersonInfo();
		// 设置上用户的session
		request.getSession().setAttribute("user", operator);
		// 解析微信回传过来的自定义参数state,由于之前进行了编码，这里需要解码一下
		String qrCodeinfo = new String(URLDecoder.decode(HttpServletRequestUtil.getString(request, "state"), "UTF-8"));
		ObjectMapper mapper = new ObjectMapper();
		WechatInfo wechatInfo;
		try {
			// 将解码后的内容用aaa去替换掉之前生成二维码的时候加入的aaa前缀，转换成WechatInfo实体类
			wechatInfo = mapper.readValue(qrCodeinfo.replace("aaa", "\""), WechatInfo.class);
		} catch (Exception e) {
			return "shop/operationfail";
		}
		// 校验二维码是否已经过期
		if (!checkQRCodeInfo(wechatInfo)) {
			return "shop/operationfail";
		}
		// 获取用户奖品映射主键
		Long userAwardId = wechatInfo.getUserAwardId();
		// 获取顾客Id
		Long customerId = wechatInfo.getCustomerId();
		// 将顾客信息，操作员信息以及奖品信息封装成userAwardMap
		UserAwardMap userAwardMap = compactUserAwardMap4Exchange(customerId, userAwardId, operator);
		if (userAwardMap != null) {
			try {
				// 检查该员工是否具有扫码权限
				if (!checkShopAuth(operator.getUserId(), userAwardMap)) {
					return "shop/operationfail";
				}
				// 修改奖品的领取状态
				UserAwardMapExecution se = userAwardMapService.modifyUserAwardMap(userAwardMap);
				if (se.getState() == UserAwardMapStateEnum.SUCCESS.getState()) {
					return "shop/operationsuccess";
				}
			} catch (RuntimeException e) {
				return "shop/operationfail";
			}

		}
		return "shop/operationfail";
	}

	/**
	 * 获取扫描二维码的店员信息
	 * @param request
	 * @return
	 */
	private WeChatAuth getOperatorInfo(HttpServletRequest request) throws IOException {
		String code = request.getParameter("code");
		WeChatAuth auth = null;
		if (null != code) {
			UserAccessToken token;
			token = WechatUtil.getUserAccessToken(code);
			String openId = token.getOpenId();
			request.getSession().setAttribute("openId", openId);
			auth = wechatAuthService.getWeChatAuthByOpenId(openId);

		}
		return auth;
	}

	/**
	 * 根据二维码携带的createTime判断其是否超过了10分钟，超过十分钟则认为过期
	 * @param wechatInfo
	 * @return
	 */
	private boolean checkQRCodeInfo(WechatInfo wechatInfo) {
		// 空值判断
		if (wechatInfo != null && wechatInfo.getUserAwardId() != null && wechatInfo.getCustomerId() != null && wechatInfo.getCreateTime() != null) {
			return System.currentTimeMillis() - wechatInfo.getCreateTime() <= 600000;
		} else {
			return false;
		}
	}

	/**
	 * 封装用户奖品映射实体类，以供扫码使用，主要将其领取状态变为已领取
	 * 
	 * @param customerId
	 * @param userAwardId
	 * @return
	 */
	private UserAwardMap compactUserAwardMap4Exchange(Long customerId, Long userAwardId, UserInfo operator) {
		UserAwardMap userAwardMap = null;
		if (customerId != null && userAwardId != null && operator != null) {
			// 获取原有userAwardMap信息
			userAwardMap = userAwardMapService.getUserAwardMapById(userAwardId).getUserAwardMap();
			userAwardMap.setUsedStatus(1);
			UserInfo customer = new UserInfo();
			customer.setUserId(customerId);
			userAwardMap.setUser(customer);
			userAwardMap.setOperator(operator);
		}
		return userAwardMap;
	}

	/**
	 * 检查员工是否具有授权权限
	 * 
	 * @param userId
	 * @param userAwardMap
	 * @return
	 */
	private boolean checkShopAuth(long userId, UserAwardMap userAwardMap) {
		// 取出该店铺所有的授权信息
		ShopAuthMapExecution shopAuthMapExecution = shopAuthMapService.listShopAuthMapByShopId(userAwardMap.getShop().getShopId(), 1, 1000);
		// 逐条遍历，看看扫描二维码的员工是否具有扫码权限
		for (ShopAuthMap shopAuthMap : shopAuthMapExecution.getShopAuthMapList()) {
			if (shopAuthMap.getEmployee().getUserId() == userId && shopAuthMap.getEnableStatus() == 1) {
				return true;
			}
		}
		return false;
	}
}
