package com.whl.o2o.service;

import com.whl.o2o.dto.ImageHolder;
import com.whl.o2o.dto.ShopExecution;
import com.whl.o2o.entity.Shop;
import com.whl.o2o.exceptions.ShopOperationException;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface ShopService {
    ShopExecution addShop(Shop shop, ImageHolder imageHolder);

    ShopExecution getByShopId(Long shopId);

    ShopExecution modifyShop(Shop shop, ImageHolder imageHolder) ;

    ShopExecution getShopList(Shop ShopCondition, int pageIndex, int pageSize);
}
