package com.whl.o2o.service;


import com.whl.o2o.dto.UserProductMapExecution;
import com.whl.o2o.entity.UserProductMap;
import com.whl.o2o.exceptions.UserProductMapOperationException;

public interface UserProductMapService {
	UserProductMapExecution listUserProductMap(UserProductMap userProductCondition, int pageIndex, int pageSize);

	UserProductMapExecution addUserProductMap(UserProductMap userProductMap);
}
