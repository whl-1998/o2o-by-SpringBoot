package com.whl.o2o.web.shopadmin;

import java.io.IOException;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.whl.o2o.dto.ShopAuthMapExecution;
import com.whl.o2o.dto.UserAccessToken;
import com.whl.o2o.dto.WechatInfo;
import com.whl.o2o.entity.Shop;
import com.whl.o2o.entity.ShopAuthMap;
import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.entity.WeChatAuth;
import com.whl.o2o.enums.ShopAuthMapStateEnum;
import com.whl.o2o.exceptions.ShopAuthMapOperationException;
import com.whl.o2o.service.ShopAuthMapService;
import com.whl.o2o.service.UserInfoService;
import com.whl.o2o.service.WeChatAuthService;
import com.whl.o2o.util.CodeUtil;
import com.whl.o2o.util.HttpServletRequestUtil;
import com.whl.o2o.util.ShortNetAddressUtil;
import com.whl.o2o.util.weixin.WechatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
@RequestMapping("/shopadmin")
public class ShopAuthManagementController {
	@Autowired
	private ShopAuthMapService shopAuthMapService;

	@RequestMapping(value = "/listshopauthmapsbyshop", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listShopAuthMapsByShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<>();
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		if (pageIndex <= 0 || pageSize <= 0) {
			throw new ShopAuthMapOperationException("非法的pageIndex或pageSize");
		}
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		if (currentShop == null || currentShop.getShopId() == null) {
			throw new ShopAuthMapOperationException("当前店铺授权实体获取失败");
		}
		// 分页取出该店铺下面的授权信息列表
		ShopAuthMapExecution se = shopAuthMapService.listShopAuthMapByShopId(currentShop.getShopId(), pageIndex, pageSize);
		modelMap.put("shopAuthMapList", se.getShopAuthMapList());
		modelMap.put("count", se.getCount());
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/getshopauthmapbyid", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopAuthMapById(@RequestParam Long shopAuthId) {
		Map<String, Object> modelMap = new HashMap<>();
		// 非空判断
		if (shopAuthId == null || shopAuthId <= 0) {
			throw new ShopAuthMapOperationException("empty shopAuthId");
		}
		// 根据前台传入的shopAuthId查找对应的授权信息
		ShopAuthMap shopAuthMap = shopAuthMapService.getShopAuthMapById(shopAuthId).getShopAuthMap();
		modelMap.put("shopAuthMap", shopAuthMap);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/modifyshopauthmap", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> modifyShopAuthMap(String shopAuthMapStr, HttpServletRequest request) throws IOException {
		Map<String, Object> modelMap = new HashMap<>();
		// 授权编辑时调用或删除/恢复授权操作的时候调用
		// 若为前者则进行验证码判断，后者则跳过验证码判断
		boolean statusChange = HttpServletRequestUtil.getBoolean(request, "statusChange");
		if (!statusChange && !CodeUtil.checkVerifyCode(request)) {
			throw new ShopAuthMapOperationException("输入了错误的验证码");
		}
		ObjectMapper mapper = new ObjectMapper();
		ShopAuthMap shopAuthMap = mapper.readValue(shopAuthMapStr, ShopAuthMap.class);
		if (shopAuthMap == null || shopAuthMap.getShopAuthId() == null) {
			throw new ShopAuthMapOperationException("请输入要修改的授权信息");
		}
		// 看看被操作的对方是否为店家本身，店家本身不支持修改
		if (!checkPermission(shopAuthMap.getShopAuthId())) {
			throw new ShopAuthMapOperationException("无法对店家本身权限做操作 (已是店铺的最高权限)");
		}
		ShopAuthMapExecution se = shopAuthMapService.modifyShopAuthMap(shopAuthMap);
		if (se.getState() != ShopAuthMapStateEnum.SUCCESS.getState()) {
			throw new ShopAuthMapOperationException(se.getStateInfo());
		}
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 检查被操作的对象是否可修改
	 * @param shopAuthId
	 * @return
	 */
	private boolean checkPermission(Long shopAuthId) {
		ShopAuthMap grantedPerson = shopAuthMapService.getShopAuthMapById(shopAuthId).getShopAuthMap();
		return grantedPerson.getTitleFlag() == 0; // 若是店家本身，不能操作
	}

	// 微信获取用户信息的api前缀
	private static String urlPrefix;
	// 微信获取用户信息的api中间部分
	private static String urlMiddle;
	// 微信获取用户信息的api后缀
	private static String urlSuffix;
	// 微信回传给的响应添加授权信息的url
	private static String authUrl;

	@Value("${wechat.prefix}")
	public void setUrlPrefix(String urlPrefix) {
		ShopAuthManagementController.urlPrefix = urlPrefix;
	}

	@Value("${wechat.middle}")
	public void setUrlMiddle(String urlMiddle) {
		ShopAuthManagementController.urlMiddle = urlMiddle;
	}

	@Value("${wechat.suffix}")
	public void setUrlSuffix(String urlSuffix) {
		ShopAuthManagementController.urlSuffix = urlSuffix;
	}

	@Value("${wechat.auth.url}")
	public void setAuthUrl(String authUrl) {
		ShopAuthManagementController.authUrl = authUrl;
	}

	/**
	 * 生成带有URL的二维码，微信扫一扫就能链接到对应的URL里面
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/generateqrcode4shopauth", method = RequestMethod.GET)
	@ResponseBody
	private void generateQRCode4ShopAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 从session里获取当前shop的信息
		Shop shop = (Shop) request.getSession().getAttribute("currentShop");
		if (shop == null || shop.getShopId() == null) {
			return;
		}
		long timpStamp = System.currentTimeMillis();// 获取当前时间戳，以保证二维码的时间有效性，精确到毫秒
		// 将店铺id和timestamp传入content，赋值到state中，这样微信获取到这些信息后会回传到授权信息的添加方法里
		// 加上aaa是为了一会的在添加信息的方法里替换这些信息使用
		String content = "{aaashopIdaaa:" + shop.getShopId() + ",aaacreateTimeaaa:" + timpStamp + "}";
		// 将content的信息先进行base64编码以避免特殊字符造成的干扰，之后拼接目标URL
		String longUrl = urlPrefix + authUrl + urlMiddle + URLEncoder.encode(content, "UTF-8") + urlSuffix;
		String shortUrl = ShortNetAddressUtil.getShortURL(longUrl);// 将目标URL转换成短的URL
		BitMatrix qRcodeImg = CodeUtil.generateQRCodeStream(shortUrl, response);// 调用二维码生成的工具类方法，传入短的URL，生成二维码
		MatrixToImageWriter.writeToStream(qRcodeImg, "png", response.getOutputStream());// 将二维码以图片流的形式输出到前端
	}

	@Autowired
	private WeChatAuthService wechatAuthService;
	@Autowired
	private UserInfoService userInfoService;

	/**
	 * 根据微信回传回来的参数添加店铺的授权信息
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/addshopauthmap", method = RequestMethod.GET)
	private String addShopAuthMap(HttpServletRequest request, HttpServletResponse response) throws IOException {
		WeChatAuth auth = getEmployeeInfo(request);// 从request里面获取微信用户的信息
		if (auth == null) {
			return null;
		}
		// 根据userId获取用户信息
		UserInfo user = userInfoService.getUserInfoById(auth.getUserInfo().getUserId()).getPersonInfo();
        // 将用户信息添加进user里
        request.getSession().setAttribute("user", user);
        // 解析微信回传过来的自定义参数state,由于之前进行了编码，这里需要解码一下
        String qrCodeinfo = new String(URLDecoder.decode(HttpServletRequestUtil.getString(request, "state"), "UTF-8"));
        ObjectMapper mapper = new ObjectMapper();
        // 将解码后的内容用aaa去替换掉之前生成二维码的时候加入的aaa前缀，转换成WechatInfo实体类
        WechatInfo wechatInfo = mapper.readValue(qrCodeinfo.replace("aaa", "\""), WechatInfo.class);
        // 校验二维码是否已经过期
        if (!checkQRCodeInfo(wechatInfo)) {
            return "shop/operationfail";
        }
        // 去重校验
        // 获取该店铺下所有的授权信息
        ShopAuthMapExecution allMapList = shopAuthMapService.listShopAuthMapByShopId(wechatInfo.getShopId(), 1, 999);
        List<ShopAuthMap> shopAuthList = allMapList.getShopAuthMapList();
        for (ShopAuthMap sm : shopAuthList) {
            if (sm.getEmployee().getUserId().equals(user.getUserId())) {
                return "shop/operationfail";
            }
        }
        try {
            // 根据获取到的内容，添加微信授权信息
            Shop shop = new Shop();
            shop.setShopId(wechatInfo.getShopId());
            ShopAuthMap shopAuthMap = ShopAuthMap.builder().shop(shop).employee(user).titleFlag(1).title("员工").build();
            ShopAuthMapExecution se = shopAuthMapService.addShopAuthMap(shopAuthMap);
            if (se.getState() == ShopAuthMapStateEnum.SUCCESS.getState()) {
                return "shop/operationsuccess";
            } else {
                return "shop/operationfail";
            }
        } catch (RuntimeException e) {
            return "shop/operationfail";
        }
	}

	/**
	 * 根据二维码携带的createTime判断其是否超过了10分钟，超过十分钟则认为过期
	 * @param wechatInfo
	 * @return
	 */
	private boolean checkQRCodeInfo(WechatInfo wechatInfo) {
		if (wechatInfo != null && wechatInfo.getShopId() != null && wechatInfo.getCreateTime() != null) {
            return System.currentTimeMillis() - wechatInfo.getCreateTime() <= 600000;
		} else {
			return false;
		}
	}

	/**
	 * 根据微信回传的code获取用户信息
	 * @param request
	 * @return
	 */
	private WeChatAuth getEmployeeInfo(HttpServletRequest request) throws IOException {
		String code = request.getParameter("code");
		WeChatAuth auth = null;
		if (null != code) {
			UserAccessToken token = WechatUtil.getUserAccessToken(code);
            String openId = token.getOpenId();
            request.getSession().setAttribute("openId", openId);
            auth = wechatAuthService.getWeChatAuthByOpenId(openId);
		}
		return auth;
	}
}
