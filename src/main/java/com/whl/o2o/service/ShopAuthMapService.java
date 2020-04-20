package com.whl.o2o.service;

import com.whl.o2o.dto.ShopAuthMapExecution;
import com.whl.o2o.entity.ShopAuthMap;
import com.whl.o2o.exceptions.ShopAuthMapOperationException;

public interface ShopAuthMapService {
	ShopAuthMapExecution listShopAuthMapByShopId(long shopId, int pageIndex, int pageSize);

	ShopAuthMapExecution getShopAuthMapById(long shopAuthId);

	ShopAuthMapExecution addShopAuthMap(ShopAuthMap shopAuthMap);

	ShopAuthMapExecution modifyShopAuthMap(ShopAuthMap shopAuthMap);
}
