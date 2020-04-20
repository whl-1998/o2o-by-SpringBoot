package com.whl.o2o.service;

import com.whl.o2o.dto.ImageHolder;
import com.whl.o2o.dto.ShopCategoryExecution;
import com.whl.o2o.entity.ShopCategory;

import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface ShopCategoryService {
    String SHOP_CATEGORY_LIST = "shopcategorylist";

    ShopCategoryExecution getShopCategoryList(ShopCategory shopCategoryCondition);

    ShopCategoryExecution addShopCategory(ShopCategory shopCategory, ImageHolder thumbnail);

    ShopCategoryExecution modifyShopCategory(ShopCategory shopCategory, ImageHolder thumbnail);

    ShopCategoryExecution getShopCategoryById(Long shopCategoryId);
}
