package com.whl.o2o.web.frontend;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.whl.o2o.dto.AwardExecution;
import com.whl.o2o.entity.Award;
import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.entity.UserShopMap;
import com.whl.o2o.exceptions.ShopOperationException;
import com.whl.o2o.exceptions.UserInfoOperationException;
import com.whl.o2o.service.AwardService;
import com.whl.o2o.service.UserShopMapService;
import com.whl.o2o.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/frontend")
public class ShopAwardController {
	@Autowired
	private AwardService awardService;
	@Autowired
	private UserShopMapService userShopMapService;

	/**
	 * 列出店铺设定的奖品列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listawardsbyshop", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listAwardsByShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 获取分页信息
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		// 获取店铺Id
		long shopId = HttpServletRequestUtil.getLong(request, "shopId");
		// 空值判断

		if (pageIndex <= 0 || pageSize <= 0) {
			throw new ShopOperationException("非法的pageIndex或pageSize");
		}
		if (shopId <= 0) {
			throw new ShopOperationException("当前店铺信息获取失败");
		}
		// 获取前端可能输入的奖品名模糊查询
		String awardName = HttpServletRequestUtil.getString(request, "awardName");
		Award awardCondition = compactAwardCondition4Search(shopId, awardName);
		// 传入查询条件分页获取奖品信息
		AwardExecution ae = awardService.getAwardList(awardCondition, pageIndex, pageSize);
		modelMap.put("awardList", ae.getAwardList());
		modelMap.put("count", ae.getCount());
		modelMap.put("success", true);
		// 从Session中获取用户信息，主要是为了显示该用户在本店铺的积分
		UserInfo user = (UserInfo) request.getSession().getAttribute("user");
		if (user == null || user.getUserId() == null) {
			throw new UserInfoOperationException("当前用户信息获取失败");
		}
		// 获取该用户在本店铺的积分信息
		UserShopMap userShopMap = userShopMapService.getUserShopMap(user.getUserId(), shopId).getUserShopMap();
		if (userShopMap == null) {
			modelMap.put("totalPoint", 0);
		} else {
			modelMap.put("totalPoint", userShopMap.getPoint());
		}
		return modelMap;
	}

	/**
	 * 封装查询条件
	 * @param shopId
	 * @param awardName
	 * @return
	 */
	private Award compactAwardCondition4Search(long shopId, String awardName) {
		Award awardCondition = new Award();
		awardCondition.setShopId(shopId);
		// 只取出可用的奖品
		awardCondition.setEnableStatus(1);
		if (awardName != null) {
			awardCondition.setAwardName(awardName);
		}
		return awardCondition;
	}
}
