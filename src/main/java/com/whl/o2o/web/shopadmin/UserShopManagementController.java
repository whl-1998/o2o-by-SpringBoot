package com.whl.o2o.web.shopadmin;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.whl.o2o.dto.UserShopMapExecution;
import com.whl.o2o.entity.Shop;
import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.entity.UserShopMap;
import com.whl.o2o.exceptions.UserShopMapOperationException;
import com.whl.o2o.service.UserShopMapService;
import com.whl.o2o.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/shopadmin")
public class UserShopManagementController {
	@Autowired
	private UserShopMapService userShopMapService;

	/**
	 * 获取某个店铺的用户积分信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listusershopmapsbyshop", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listUserShopMapsByShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<>();
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		if (pageIndex <= 0 || pageSize <= 0) {
			throw new UserShopMapOperationException("非法的pageIndex或pageSize");
		}
		if (currentShop == null || currentShop.getShopId() == null) {
			throw new UserShopMapOperationException("当前店铺信息获取失败");
		}
		UserShopMap userShopMapCondition = new UserShopMap();
		userShopMapCondition.setShop(currentShop);
		String userName = HttpServletRequestUtil.getString(request, "userName");
		if (userName != null) {// 若传入顾客名，则按照顾客名模糊查询
			UserInfo customer = new UserInfo();
			customer.setUsername(userName);
			userShopMapCondition.setUser(customer);
		}
		UserShopMapExecution ue = userShopMapService.listUserShopMap(userShopMapCondition, pageIndex, pageSize);
		modelMap.put("userShopMapList", ue.getUserShopMapList());
		modelMap.put("count", ue.getCount());
		modelMap.put("success", true);
		return modelMap;
	}
}
