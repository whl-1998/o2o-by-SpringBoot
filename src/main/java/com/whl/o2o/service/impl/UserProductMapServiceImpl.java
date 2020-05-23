package com.whl.o2o.service.impl;

import java.util.Date;
import java.util.List;

import com.whl.o2o.dao.UserProductMapDao;
import com.whl.o2o.dao.UserShopMapDao;
import com.whl.o2o.dto.UserProductMapExecution;
import com.whl.o2o.entity.Shop;
import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.entity.UserProductMap;
import com.whl.o2o.entity.UserShopMap;
import com.whl.o2o.enums.UserProductMapStateEnum;
import com.whl.o2o.exceptions.UserProductMapOperationException;
import com.whl.o2o.service.UserProductMapService;
import com.whl.o2o.util.PageCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserProductMapServiceImpl implements UserProductMapService {
	@Autowired
	private UserProductMapDao userProductMapDao;
	@Autowired
	private UserShopMapDao userShopMapDao;

	private final static Logger logger = LoggerFactory.getLogger(UserProductMapServiceImpl.class);

	@Override
	public UserProductMapExecution listUserProductMap(UserProductMap userProductCondition, int pageIndex, int pageSize) {
		if (userProductCondition == null || pageIndex <= 0 || pageSize <= 0) {
			return new UserProductMapExecution(UserProductMapStateEnum.EMPTY);
		}
		int beginIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
		List<UserProductMap> userProductMapList = userProductMapDao.queryUserProductMapList(userProductCondition, beginIndex, pageSize);
		int count = userProductMapDao.queryUserProductMapCount(userProductCondition);
		UserProductMapExecution se = new UserProductMapExecution();
		if (count == userProductMapList.size()) {
			se.setUserProductMapList(userProductMapList);
			se.setCount(count);
		} else {
			return new UserProductMapExecution(UserProductMapStateEnum.INNER_ERROR);
		}
		return se;
	}

	@Override
	@Transactional
	public UserProductMapExecution addUserProductMap(UserProductMap userProductMap) {
		// 空值判断，主要确保顾客Id，店铺Id以及操作员Id非空
		if (userProductMap != null && userProductMap.getUser().getUserId() != null
				&& userProductMap.getShop().getShopId() != null && userProductMap.getOperator().getUserId() != null) {
			// 设定默认值
			userProductMap.setCreateTime(new Date());
			try {
				// 添加消费记录
				int effectedNum = userProductMapDao.insertUserProductMap(userProductMap);
				if (effectedNum <= 0) {
					throw new UserProductMapOperationException("添加消费记录失败");
				}
				// 若本次消费能够积分
				if (userProductMap.getPoint() != null && userProductMap.getPoint() > 0) {
					// 查询该顾客是否在店铺消费过
					UserShopMap userShopMap = userShopMapDao.queryUserShopMap(userProductMap.getUser().getUserId(), userProductMap.getShop().getShopId());
					if (userShopMap != null && userShopMap.getUserShopId() != null) {
						// 若之前消费过，即有过积分记录，则进行总积分的更新操作
						userShopMap.setPoint(userShopMap.getPoint() + userProductMap.getPoint());
						effectedNum = userShopMapDao.updateUserShopMapPoint(userShopMap);
						if (effectedNum <= 0) {
							throw new UserProductMapOperationException("更新积分信息失败");
						}
					} else {
						// 在店铺没有过消费记录，添加一条店铺积分信息(就跟初始化会员一样)
						userShopMap = compactUserShopMap4Add(userProductMap.getUser().getUserId(),
								userProductMap.getShop().getShopId(), userProductMap.getPoint());
						effectedNum = userShopMapDao.insertUserShopMap(userShopMap);
						if (effectedNum <= 0) {
							throw new UserProductMapOperationException("积分信息创建失败");
						}
					}
				}
				return new UserProductMapExecution(UserProductMapStateEnum.SUCCESS, userProductMap);
			} catch (Exception e) {
				throw new UserProductMapOperationException("添加授权失败:" + e.toString());
			}
		} else {
			return new UserProductMapExecution(UserProductMapStateEnum.EMPTY);
		}
	}

	/**
	 * 封装顾客积分信息
	 * @param userId
	 * @param shopId
	 * @param point
	 * @return
	 */
	private UserShopMap compactUserShopMap4Add(long userId, long shopId, int point) {
		if (userId <= 0 || shopId <= 0) {
			return null;
		}
		UserInfo customer = new UserInfo();
		customer.setUserId(userId);
		Shop shop = new Shop();
		shop.setShopId(shopId);
		UserShopMap userShopMap = UserShopMap.builder().point(point).createTime(new Date()).shop(shop).user(customer).build();
		return userShopMap;
	}
}
