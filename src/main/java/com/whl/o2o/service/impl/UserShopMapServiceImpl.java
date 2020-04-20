package com.whl.o2o.service.impl;

import java.util.List;

import com.whl.o2o.dao.UserShopMapDao;
import com.whl.o2o.dto.UserShopMapExecution;
import com.whl.o2o.entity.UserShopMap;
import com.whl.o2o.enums.UserShopMapStateEnum;
import com.whl.o2o.service.UserShopMapService;
import com.whl.o2o.util.PageCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserShopMapServiceImpl implements UserShopMapService {
	@Autowired
	private UserShopMapDao userShopMapDao;

	@Override
	public UserShopMapExecution listUserShopMap(UserShopMap userShopMapCondition, int pageIndex, int pageSize) {
		if (userShopMapCondition == null || pageIndex <= 0 || pageSize <= 0) {
			return new UserShopMapExecution(UserShopMapStateEnum.EMPTY);
		}
		int beginIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
		List<UserShopMap> userShopMapList = userShopMapDao.queryUserShopMapList(userShopMapCondition, beginIndex, pageSize);
		int count = userShopMapDao.queryUserShopMapCount(userShopMapCondition);
		UserShopMapExecution ue = new UserShopMapExecution();
		if (count == userShopMapList.size()) {
			ue.setUserShopMapList(userShopMapList);
			ue.setCount(count);
		} else {
			return new UserShopMapExecution(UserShopMapStateEnum.INNER_ERROR);
		}
		return ue;
	}

	@Override
	public UserShopMapExecution getUserShopMap(long userId, long shopId) {
		if (userId <= 0 || shopId <= 0) {
			return new UserShopMapExecution(UserShopMapStateEnum.EMPTY);
		}
		return new UserShopMapExecution(UserShopMapStateEnum.SUCCESS, userShopMapDao.queryUserShopMap(userId, shopId));
	}
}
