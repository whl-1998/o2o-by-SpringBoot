package com.whl.o2o.service;

import com.whl.o2o.dto.UserShopMapExecution;
import com.whl.o2o.entity.UserShopMap;

public interface UserShopMapService {
	UserShopMapExecution listUserShopMap(UserShopMap userShopMapCondition, int pageIndex, int pageSize);

	UserShopMapExecution getUserShopMap(long userId, long shopId);
}
