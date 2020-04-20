package com.whl.o2o.web.frontend;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.whl.o2o.dto.UserAwardMapExecution;
import com.whl.o2o.entity.Award;
import com.whl.o2o.entity.Shop;
import com.whl.o2o.entity.UserAwardMap;
import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.enums.UserAwardMapStateEnum;
import com.whl.o2o.exceptions.AwardOperationException;
import com.whl.o2o.service.AwardService;
import com.whl.o2o.service.UserAwardMapService;
import com.whl.o2o.service.UserInfoService;
import com.whl.o2o.util.CodeUtil;
import com.whl.o2o.util.HttpServletRequestUtil;
import com.whl.o2o.util.ShortNetAddressUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;


@Controller
@RequestMapping("/frontend")
public class MyAwardController {
	@Autowired
	private UserAwardMapService userAwardMapService;
	@Autowired
	private AwardService awardService;
	@Autowired
	private UserInfoService personInfoService;

	/**
	 * 根据顾客奖品映射Id获取单条顾客奖品的映射信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getawardbyuserawardid", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getAwardbyId(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<>();
		long userAwardId = HttpServletRequestUtil.getLong(request, "userAwardId");// 获取前端传递过来的userAwardId
		if (userAwardId <= 0) {
			throw new AwardOperationException("获取奖品失败");
		}
		UserAwardMap userAwardMap = userAwardMapService.getUserAwardMapById(userAwardId).getUserAwardMap();
		Award award = awardService.getAwardById(userAwardMap.getAward().getAwardId()).getAward();
		// 将奖品信息和领取状态返回给前端
		modelMap.put("award", award);
		modelMap.put("usedStatus", userAwardMap.getUsedStatus());
		modelMap.put("userAwardMap", userAwardMap);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取顾客的兑换列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listuserawardmapsbycustomer", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listUserAwardMapsByCustomer(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<>();
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		UserInfo user = (UserInfo) request.getSession().getAttribute("user");
		if (pageIndex <= 0 || pageSize <= 0) {
			throw new AwardOperationException("非法的pageIndex或pageSize");
		}
		if (user == null || user.getUserId() == null) {
			throw new AwardOperationException("当前用户信息获取失败");
		}
		UserAwardMap userAwardMapCondition = new UserAwardMap();
		userAwardMapCondition.setUser(user);
		long shopId = HttpServletRequestUtil.getLong(request, "shopId");
		if (shopId > -1) {
			// 若店铺id为非空，则将其添加进查询条件，即查询该用户在某个店铺的兑换信息
			Shop shop = new Shop();
			shop.setShopId(shopId);
			userAwardMapCondition.setShop(shop);
		}
		String awardName = HttpServletRequestUtil.getString(request, "awardName");
		if (awardName != null) {
			// 若奖品名为非空，则将其添加进查询条件里进行模糊查询
			Award award = new Award();
			award.setAwardName(awardName);
			userAwardMapCondition.setAward(award);
		}
		// 根据传入的查询条件分页获取用户奖品映射信息
		UserAwardMapExecution ue = userAwardMapService.listUserAwardMap(userAwardMapCondition, pageIndex, pageSize);
		modelMap.put("userAwardMapList", ue.getUserAwardMapList());
		modelMap.put("count", ue.getCount());
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 在线兑换礼品
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/adduserawardmap", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> addUserAwardMap(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<>();
		UserInfo user = (UserInfo) request.getSession().getAttribute("user");// 从session中获取用户信息
		Long awardId = HttpServletRequestUtil.getLong(request, "awardId");// 从前端请求中获取奖品Id
		UserAwardMap userAwardMap = compactUserAwardMap4Add(user, awardId);// 封装成用户奖品映射对象
		if (userAwardMap == null) {
			throw new AwardOperationException("请选择领取的奖品");
		}
		// 添加兑换信息
		UserAwardMapExecution se = userAwardMapService.addUserAwardMap(userAwardMap);
		if (se.getState() != UserAwardMapStateEnum.SUCCESS.getState()) {
			throw new AwardOperationException(se.getStateInfo());
		}
		modelMap.put("success", true);
		return modelMap;
	}

	// 微信获取用户信息的api前缀
	private static String urlPrefix;
	// 微信获取用户信息的api中间部分
	private static String urlMiddle;
	// 微信获取用户信息的api后缀
	private static String urlSuffix;
	// 微信回传给的响应添加用户奖品映射信息的url
	private static String exchangeUrl;

	@Value("${wechat.prefix}")
	public void setUrlPrefix(String urlPrefix) {
		MyAwardController.urlPrefix = urlPrefix;
	}

	@Value("${wechat.middle}")
	public void setUrlMiddle(String urlMiddle) {
		MyAwardController.urlMiddle = urlMiddle;
	}

	@Value("${wechat.suffix}")
	public void setUrlSuffix(String urlSuffix) {
		MyAwardController.urlSuffix = urlSuffix;
	}

	@Value("${wechat.exchange.url}")
	public void setExchangeUrl(String exchangeUrl) {
		MyAwardController.exchangeUrl = exchangeUrl;
	}

	/**
	 * 生成奖品的领取二维码，供操作员扫描，证明已领取，微信扫一扫就能链接到对应的URL里面
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/generateqrcode4award", method = RequestMethod.GET)
	@ResponseBody
	private void generateQRCode4Product(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取前端传递过来的用户奖品映射Id
		long userAwardId = HttpServletRequestUtil.getLong(request, "userAwardId");
		// 根据Id获取顾客奖品映射实体类对象
		UserAwardMap userAwardMap = userAwardMapService.getUserAwardMapById(userAwardId).getUserAwardMap();
		// 从session中获取顾客的信息
		UserInfo user = (UserInfo) request.getSession().getAttribute("user");
		// 空值判断
		if (userAwardMap != null && user != null && user.getUserId() != null && userAwardMap.getUser().getUserId() == user.getUserId()) {
			// 获取当前时间戳，以保证二维码的时间有效性，精确到毫秒
			long timpStamp = System.currentTimeMillis();
			// 将顾客奖品映射id，顾客Id和timestamp传入content，赋值到state中，这样微信获取到这些信息后会回传到用户奖品映射信息的添加方法里
			// 加上aaa是为了一会的在添加信息的方法里替换这些信息使用
			String content = "{aaauserAwardIdaaa:" + userAwardId + ",aaacustomerIdaaa:" + user.getUserId() + ",aaacreateTimeaaa:" + timpStamp + "}";
			// 将content的信息先进行base64编码以避免特殊字符造成的干扰，之后拼接目标URL
			String longUrl = urlPrefix + exchangeUrl + urlMiddle + URLEncoder.encode(content, "UTF-8") + urlSuffix;
			// 将目标URL转换成短的URL
			String shortUrl = ShortNetAddressUtil.getShortURL(longUrl);
			// 调用二维码生成的工具类方法，传入短的URL，生成二维码
			BitMatrix qRcodeImg = CodeUtil.generateQRCodeStream(shortUrl, response);
			// 将二维码以图片流的形式输出到前端
			MatrixToImageWriter.writeToStream(qRcodeImg, "png", response.getOutputStream());
		}
	}

	/**
	 * 封装用户奖品映射实体类
	 * @param user
	 * @param awardId
	 * @return
	 */
	private UserAwardMap compactUserAwardMap4Add(UserInfo user, Long awardId) {
		UserAwardMap userAwardMap = null;
		if (user != null && user.getUserId() != null && awardId != -1) {
			userAwardMap = new UserAwardMap();
			// 根据用户Id获取用户实体类对象
			UserInfo personInfo = personInfoService.getUserInfoById(user.getUserId()).getPersonInfo();
			// 根据奖品Id获取奖品实体类对象
			Award award = awardService.getAwardById(awardId).getAward();
			userAwardMap.setUser(personInfo);
			userAwardMap.setAward(award);
			Shop shop = new Shop();
			shop.setShopId(award.getShopId());
			userAwardMap.setShop(shop);
			// 设置积分
			userAwardMap.setPoint(award.getPoint());
			userAwardMap.setCreateTime(new Date());
			// 设置兑换状态为已领取
			userAwardMap.setUsedStatus(1);
		}
		return userAwardMap;
	}
}
