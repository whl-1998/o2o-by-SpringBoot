package com.whl.o2o.dao;

import com.whl.o2o.entity.ShopCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface ShopCategoryDao {
    List<ShopCategory> queryShopCategory(@Param("shopCategoryCondition") ShopCategory shopCategoryCondition);

    ShopCategory queryShopCategoryById(long shopCategoryId);

    List<ShopCategory> queryShopCategoryByIds(List<Long> shopCategoryIdList);

    int insertShopCategory(ShopCategory shopCategory);

    int updateShopCategory(ShopCategory shopCategory);

    int deleteShopCategory(long shopCategoryId);

    int batchDeleteShopCategory(List<Long> shopCategoryIdList);
}
