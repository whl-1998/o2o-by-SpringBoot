package com.whl.o2o.web.shopadmin;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.whl.o2o.dto.*;
import com.whl.o2o.entity.*;
import com.whl.o2o.enums.UserProductMapStateEnum;
import com.whl.o2o.exceptions.UserProductMapOperationException;
import com.whl.o2o.service.*;
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
public class UserProductManagementController {
	@Autowired
	private UserProductMapService userProductMapService;
	@Autowired
	private ProductSellDailyService productSellDailyService;
	@Autowired
	private WeChatAuthService wechatAuthService;
	@Autowired
	private ShopAuthMapService shopAuthMapService;
	@Autowired
	private ProductService productService;

	@RequestMapping(value = "/listuserproductmapsbyshop", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listUserProductMapsByShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<>();
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		if (pageIndex <= 0 || pageSize <= 0) {
			throw new UserProductMapOperationException("非法的pageIndex或pageSize");
		}
		if (currentShop == null || currentShop.getShopId() == null) {
			throw new UserProductMapOperationException("当前店铺信息获取失败");
		}
		// 添加查询条件
		UserProductMap userProductMapCondition = new UserProductMap();
		userProductMapCondition.setShop(currentShop);
		String productName = HttpServletRequestUtil.getString(request, "productName");
		if (productName != null) {// 若前端想按照商品名模糊查询，则传入productName
			Product product = new Product();
			product.setProductName(productName);
			userProductMapCondition.setProduct(product);
		}
		UserProductMapExecution ue = userProductMapService.listUserProductMap(userProductMapCondition, pageIndex, pageSize);
		modelMap.put("userProductMapList", ue.getUserProductMapList());
		modelMap.put("count", ue.getCount());
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/listproductselldailyinfobyshop", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listProductSellDailyInfobyShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<>();
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		if (currentShop == null || currentShop.getShopId() == null) {
			throw new UserProductMapOperationException("当前店铺信息获取失败");
		}
		ProductSellDaily productSellDailyCondition = new ProductSellDaily();// 添加查询条件
		productSellDailyCondition.setShop(currentShop);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);// 获取昨天的日期
		Date endTime = calendar.getTime();
		calendar.add(Calendar.DATE, -6);// 获取七天前的日期
		Date beginTime = calendar.getTime();
		// 根据传入的查询条件获取该店铺的商品销售情况
		List<ProductSellDaily> productSellDailyList = productSellDailyService.listProductSellDaily(productSellDailyCondition, beginTime, endTime);
		// 指定日期格式
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 商品名列表，保证唯一性
		LinkedHashSet<String> legendData = new LinkedHashSet<>();
		// x轴数据
		LinkedHashSet<String> xData = new LinkedHashSet<>();
		// 定义series
		List<EchartSeries> series = new ArrayList<>();
		// 日销量列表
		List<Integer> totalList = new ArrayList<>();
		// 当前商品名，默认为空
		String currentProductName = "";
		for (int i = 0; i < productSellDailyList.size(); i++) {
			ProductSellDaily productSellDaily = productSellDailyList.get(i);
			// 自动去重
			legendData.add(productSellDaily.getProduct().getProductName());
			xData.add(sdf.format(productSellDaily.getCreateTime()));
			if (!currentProductName.equals(productSellDaily.getProduct().getProductName()) && !currentProductName.isEmpty()) {
				// 如果currentProductName不等于获取的商品名，或者已遍历到列表的末尾，且currentProductName不为空，
				// 则是遍历到下一个商品的日销量信息了, 将前一轮遍历的信息放入series当中，
				// 包括了商品名以及与商品对应的统计日期以及当日销量
				EchartSeries es = new EchartSeries();
				es.setName(currentProductName);
				es.setData(totalList.subList(0, totalList.size()));
				series.add(es);
				// 重置totalList
				totalList = new ArrayList<Integer>();
				// 变换下currentProductId为当前的productId
				currentProductName = productSellDaily.getProduct().getProductName();
				// 继续添加新的值
				totalList.add(productSellDaily.getTotal());
			} else {
				// 如果还是当前的productId则继续添加新值
				totalList.add(productSellDaily.getTotal());
				currentProductName = productSellDaily.getProduct().getProductName();
			}
			// 队列之末，需要将最后的一个商品销量信息也添加上
			if (i == productSellDailyList.size() - 1) {
				EchartSeries es = new EchartSeries();
				es.setName(currentProductName);
				es.setData(totalList.subList(0, totalList.size()));
				series.add(es);
			}
		}
		modelMap.put("series", series);
		modelMap.put("legendData", legendData);
		// 拼接出xAxis
		List<EchartXAxis> xAxis = new ArrayList<>();
		EchartXAxis exa = new EchartXAxis();
		exa.setData(xData);
		xAxis.add(exa);
		modelMap.put("xAxis", xAxis);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/adduserproductmap", method = RequestMethod.GET)
	private String addUserProductMap(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取微信授权信息
		WeChatAuth auth = getOperatorInfo(request);
		if (auth != null) {
			UserInfo user = auth.getUserInfo();
//			UserInfo operator = auth.getUserInfo();
			request.getSession().setAttribute("user", user);
			// 获取二维码里state携带的content信息并解码
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
//			if (!checkQRCodeInfo(wechatInfo)) {
//				return "shop/operationfail";
//			}
			// 获取添加消费记录所需要的参数并组建成userproductmap实例
			Long productId = wechatInfo.getProductId();
			Long customerId = wechatInfo.getCustomerId();
			UserProductMap userProductMap = compactUserProductMap4Add(customerId, productId, auth.getUserInfo());
			// 空值校验
			if (userProductMap != null && customerId != -1) {
				try {
//					if (!checkShopAuth(operator.getUserId(), userProductMap)) {
//						return "shop/operationfail";
//					}
					// 添加消费记录
					UserProductMapExecution se = userProductMapService.addUserProductMap(userProductMap);
					if (se.getState() == UserProductMapStateEnum.SUCCESS.getState()) {
						return "shop/operationsuccess";
					}
				} catch (RuntimeException e) {
					return "shop/operationfail";
				}
			}
		}
		return "shop/operationfail";
	}

	/**
	 * 根据code获取UserAccessToken，进而通过token里的openId获取微信用户信息
	 * @param request
	 * @return
	 */
	private WeChatAuth getOperatorInfo(HttpServletRequest request) {
		String code = request.getParameter("code");
		WeChatAuth auth = null;
		if (null != code) {
			UserAccessToken token;
			try {
				token = WechatUtil.getUserAccessToken(code);
				String openId = token.getOpenId();
				request.getSession().setAttribute("openId", openId);
				auth = wechatAuthService.getWeChatAuthByOpenId(openId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return auth;
	}

	/**
	 * 根据二维码携带的createTime判断其是否超过了10分钟，超过十分钟则认为过期
	 * @param wechatInfo
	 * @return
	 */
	private boolean checkQRCodeInfo(WechatInfo wechatInfo) {
		if (wechatInfo != null && wechatInfo.getProductId() != null && wechatInfo.getCustomerId() != null && wechatInfo.getCreateTime() != null) {
			return System.currentTimeMillis() - wechatInfo.getCreateTime() <= 600000;
		} else {
			return false;
		}
	}

	/**
	 * 根据传入的customerId, productId以及操作员信息组建用户消费记录
	 * @param customerId
	 * @param productId
	 * @param operator
	 * @return
	 */
	private UserProductMap compactUserProductMap4Add(Long customerId, Long productId, UserInfo operator) {
		UserProductMap userProductMap = null;
		if (customerId != null && productId != null) {
			userProductMap = new UserProductMap();
			UserInfo customer = new UserInfo();
			customer.setUserId(customerId);
			// 主要为了获取商品积分
			Product product = productService.getProductById(productId).getProduct();
			userProductMap.setProduct(product);
			userProductMap.setShop(product.getShop());
			userProductMap.setUser(customer);
			userProductMap.setPoint(product.getPoint());
			userProductMap.setCreateTime(new Date());
			userProductMap.setOperator(operator);
		}
		return userProductMap;
	}

	/**
	 * 检查扫码的人员是否有操作权限
	 * @param userId
	 * @param userProductMap
	 * @return
	 */
	private boolean checkShopAuth(long userId, UserProductMap userProductMap) {
		// 获取该店铺的所有授权信息
		ShopAuthMapExecution shopAuthMapExecution = shopAuthMapService.listShopAuthMapByShopId(userProductMap.getShop().getShopId(), 1, 1000);
		for (ShopAuthMap shopAuthMap : shopAuthMapExecution.getShopAuthMapList()) {
			// 看看是否给过该人员进行授权
			if (shopAuthMap.getEmployee().getUserId() == userId) {
				return true;
			}
		}
		return false;
	}
}
