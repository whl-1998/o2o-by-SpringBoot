package com.whl.o2o.web.frontend;


import com.whl.o2o.dto.ProductExecution;
import com.whl.o2o.entity.Product;
import com.whl.o2o.entity.ProductCategory;
import com.whl.o2o.entity.Shop;
import com.whl.o2o.exceptions.ShopOperationException;
import com.whl.o2o.service.ProductCategoryService;
import com.whl.o2o.service.ProductService;
import com.whl.o2o.service.ShopService;
import com.whl.o2o.util.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:商品详情页Controller
 */
@Controller
@RequestMapping("/frontend")
public class ShopDetailController {
    @Autowired
    private ShopService shopService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductService productService;


    @RequestMapping(value = "/listshopdetailpageinfo", method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> listShopDetailPageInfo(HttpServletRequest request){
        Map<String,Object> modelMap = new HashMap<>();
        //获取request的shopId
        long shopId = HttpServletRequestUtil.getLong(request, "shopId");
        Shop shop;
        List<ProductCategory> productCategoryList;
        if (shopId <= 0) {
            throw new ShopOperationException("当前店铺信息获取失败");
        }
        shop = shopService.getByShopId(shopId).getShop();
        productCategoryList = productCategoryService.getProductCategoryList(shopId).getProductCategoryList();
        modelMap.put("shop", shop);
        modelMap.put("productCategoryList", productCategoryList);
        modelMap.put("success", true);
        return modelMap;
    }

    /**
     * 展示店铺下的所有商品
     * @param request
     * @return
     */
    @RequestMapping(value = "listproductsbyshop", method = RequestMethod.GET)
    @ResponseBody
    private Map<String,Object> listproductbyshop(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
        long shopId = HttpServletRequestUtil.getLong(request, "shopId");
        if (pageIndex <= 0 || pageSize <= 0) {
            throw new ShopOperationException("非法的pageIndex或pageSize");
        }
        if (shopId <= 0) {
            throw new ShopOperationException("当前店铺信息获取失败");
        }
        long productCategoryId = HttpServletRequestUtil.getLong(request, "productCategoryId");
        String productName = HttpServletRequestUtil.getString(request, "productName");
        Product productCondition = compactProductCondition4Search(shopId, productCategoryId, productName);
        ProductExecution productExecution = productService.getProductList(productCondition, pageIndex, pageSize);
        modelMap.put("productList", productExecution.getProductList());
        modelMap.put("count", productExecution.getCount());
        modelMap.put("success", true);
        return modelMap;
    }

    private Product compactProductCondition4Search(long shopId, long productCategoryId, String productName) {
        Product productCondition = new Product();
        Shop shop = new Shop();
        shop.setShopId(shopId);
        productCondition.setShop(shop);
        if (productCategoryId != -1L) {
            ProductCategory productCategory = new ProductCategory();
            productCategory.setProductCategoryId(productCategoryId);
            productCondition.setProductCategory(productCategory);
        }
        if (productName != null) {
            productCondition.setProductName(productName);
        }
        productCondition.setEnableStatus(1);
        return productCondition;
    }
}
