package com.whl.o2o.web.frontend;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.whl.o2o.dto.UserProductMapExecution;
import com.whl.o2o.entity.Product;
import com.whl.o2o.entity.Shop;
import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.entity.UserProductMap;
import com.whl.o2o.exceptions.UserProductMapOperationException;
import com.whl.o2o.service.UserProductMapService;
import com.whl.o2o.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/frontend")
public class MyProductController {
	@Autowired
	private UserProductMapService userProductMapService;

	/**
	 * 列出某个顾客的商品消费信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listuserproductmapsbycustomer", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listUserProductMapsByCustomer(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 获取分页信息
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		// 从session里获取顾客信息
		UserInfo user = (UserInfo) request.getSession().getAttribute("user");
		// 空值判断
		if (pageIndex <= 0 || pageSize <= 0) {
			throw new UserProductMapOperationException("非法的pageIndex或pageSize");
		}
		if (user == null || user.getUserId() == null) {
			throw new UserProductMapOperationException("当前用户信息获取失败");
		}
		UserProductMap userProductMapCondition = new UserProductMap();
		userProductMapCondition.setUser(user);
		long shopId = HttpServletRequestUtil.getLong(request, "shopId");
		if (shopId > -1) {// 若传入店铺信息，则列出某个店铺下该顾客的消费历史
			Shop shop = new Shop();
			shop.setShopId(shopId);
			userProductMapCondition.setShop(shop);
		}
		String productName = HttpServletRequestUtil.getString(request, "productName");
		if (productName != null) {// 若传入的商品名不为空，则按照商品名模糊查询
			Product product = new Product();
			product.setProductName(productName);
			userProductMapCondition.setProduct(product);
		}
		// 根据查询条件分页返回用户消费信息
		UserProductMapExecution ue = userProductMapService.listUserProductMap(userProductMapCondition, pageIndex, pageSize);
		modelMap.put("userProductMapList", ue.getUserProductMapList());
		modelMap.put("count", ue.getCount());
		modelMap.put("success", true);
		return modelMap;
	}
}
