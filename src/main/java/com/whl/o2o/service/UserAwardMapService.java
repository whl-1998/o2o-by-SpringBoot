package com.whl.o2o.service;

import com.whl.o2o.dto.UserAwardMapExecution;
import com.whl.o2o.entity.UserAwardMap;
import com.whl.o2o.exceptions.UserAwardMapOperationException;

public interface UserAwardMapService {

	UserAwardMapExecution listUserAwardMap(UserAwardMap userAwardCondition, int pageIndex, int pageSize);

	UserAwardMapExecution listReceivedUserAwardMap(UserAwardMap userAwardCondition, int pageIndex, int pageSize);

	UserAwardMapExecution getUserAwardMapById(long userAwardMapId);

	/**
	 * 添加用户已经兑换的奖品实体, 用于兑换操作之后的数据处理
	 * @param userAwardMap
	 * @return
	 */
	UserAwardMapExecution addUserAwardMap(UserAwardMap userAwardMap);

	UserAwardMapExecution modifyUserAwardMap(UserAwardMap userAwardMap);
}
