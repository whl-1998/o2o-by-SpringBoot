package com.whl.o2o.web.shopadmin;

import com.whl.o2o.dto.ProductCategoryExecution;
import com.whl.o2o.dto.Result;
import com.whl.o2o.entity.Product;
import com.whl.o2o.entity.ProductCategory;
import com.whl.o2o.entity.Shop;
import com.whl.o2o.enums.ProductCategoryStateEnum;
import com.whl.o2o.exceptions.ProductCategoryOperationException;
import com.whl.o2o.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/shopadmin")
public class ProductCategoryManagementController {
	@Autowired
	private ProductCategoryService productCategoryService;

	@RequestMapping(value = "/getproductcategorylist", method = RequestMethod.GET)
	@ResponseBody
	private Result<List<ProductCategory>> getProductCategoryList(HttpServletRequest request) {
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		List<ProductCategory> list;
		if (currentShop != null && currentShop.getShopId() > 0) {
			list = productCategoryService.getProductCategoryList(currentShop.getShopId()).getProductCategoryList();
			return new Result<>(true, list);
		} else {
			ProductCategoryStateEnum ps = ProductCategoryStateEnum.INNER_ERROR;
			return new Result<>(false, ps.getStateInfo(), ps.getState());
		}
	}

	@RequestMapping(value = "/addproductcategorys", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> addProductCategorys(@RequestBody List<ProductCategory> productCategoryList, HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<>();
		if (productCategoryList == null || productCategoryList.size() <= 0) {
			throw new ProductCategoryOperationException("添加的商品分类为0");
		}
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		if (currentShop == null || currentShop.getShopId() <= 0) {
			throw new ProductCategoryOperationException("当前店铺信息过期, 请重新登录");
		}
		for (ProductCategory pc : productCategoryList) {
			pc.setShopId(currentShop.getShopId());
		}
		ProductCategoryExecution pe = productCategoryService.batchAddProductCategory(productCategoryList);
		if (pe.getState() != ProductCategoryStateEnum.SUCCESS.getState()) {
			throw new ProductCategoryOperationException(pe.getStateInfo());
		}
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/removeproductcategory", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> removeProductCategory(Long productCategoryId, HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<>();
		if (productCategoryId == null || productCategoryId <= 0) {
			throw new ProductCategoryOperationException("删除的商品类别出错");
		}
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		ProductCategoryExecution pe = productCategoryService.deleteProductCategory(productCategoryId, currentShop.getShopId());
		if (pe.getState() != ProductCategoryStateEnum.SUCCESS.getState()) {
			throw new ProductCategoryOperationException(pe.getStateInfo());
		}
		modelMap.put("success", true);
		return modelMap;
	}	
}
