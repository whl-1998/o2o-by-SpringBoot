package com.whl.o2o.dao;

import com.whl.o2o.entity.Shop;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface ShopDao {
    int insertShop(Shop shop);

    int updateShop(Shop shop);

    Shop queryByShopId(Long shopId);

    /**
     * 分页查询店铺,输入:店铺名,店铺类别,区域id,owner,店铺状态
     * @param shopCondition
     * @param rowIndex  从第几行开始取值
     * @param pageSize  返回的条数
     * @return
     */
    List<Shop> queryShopList(@Param("shopCondition") Shop shopCondition, @Param("rowIndex") int rowIndex, @Param("pageSize") int pageSize);

    int queryShopCount(@Param("shopCondition") Shop shopCondition);
}
