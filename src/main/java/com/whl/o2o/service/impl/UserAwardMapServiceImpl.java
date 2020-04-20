package com.whl.o2o.service.impl;

import java.util.Date;
import java.util.List;

import com.whl.o2o.dao.UserAwardMapDao;
import com.whl.o2o.dao.UserShopMapDao;
import com.whl.o2o.dto.UserAwardMapExecution;
import com.whl.o2o.entity.UserAwardMap;
import com.whl.o2o.entity.UserShopMap;
import com.whl.o2o.enums.UserAwardMapStateEnum;
import com.whl.o2o.exceptions.UserAwardMapOperationException;
import com.whl.o2o.service.UserAwardMapService;
import com.whl.o2o.util.PageCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Email;


@Service
public class UserAwardMapServiceImpl implements UserAwardMapService {
	@Autowired
	private UserAwardMapDao userAwardMapDao;
	@Autowired
	private UserShopMapDao userShopMapDao;

    private final static Logger logger = LoggerFactory.getLogger(UserAwardMapServiceImpl.class);

	@Override
	public UserAwardMapExecution listUserAwardMap(UserAwardMap userAwardCondition, int pageIndex, int pageSize) {
		if (userAwardCondition == null || pageIndex <= 0 || pageSize <= 0) {
            return new UserAwardMapExecution(UserAwardMapStateEnum.EMPTY);
        }
        int beginIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
        List<UserAwardMap> userAwardMapList = userAwardMapDao.queryUserAwardMapList(userAwardCondition, beginIndex, pageSize);
        int count = userAwardMapDao.queryUserAwardMapCount(userAwardCondition);
        UserAwardMapExecution ue = new UserAwardMapExecution();
        if (count == userAwardMapList.size()) {
            ue.setUserAwardMapList(userAwardMapList);
            ue.setCount(count);
        } else {
            return new UserAwardMapExecution(UserAwardMapStateEnum.INNER_ERROR);
        }
        return ue;
	}

	@Override
	public UserAwardMapExecution listReceivedUserAwardMap(UserAwardMap userAwardCondition, int pageIndex, int pageSize) {
        if (userAwardCondition == null || pageIndex <= 0 || pageSize <= 0) {
            return new UserAwardMapExecution(UserAwardMapStateEnum.EMPTY);
        }
        int beginIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
        // 根据查询条件分页返回用户与奖品的映射信息列表(用户领取奖品的信息列表)
        List<UserAwardMap> userAwardMapList = userAwardMapDao.queryReceivedUserAwardMapList(userAwardCondition, beginIndex, pageSize);
        int count = userAwardMapDao.queryUserAwardMapCount(userAwardCondition);
        UserAwardMapExecution ue = new UserAwardMapExecution();
        if (count == userAwardMapList.size()) {
            ue.setUserAwardMapList(userAwardMapList);
            ue.setCount(count);
        } else {
            return new UserAwardMapExecution(UserAwardMapStateEnum.INNER_ERROR);
        }
        return ue;
	}

	@Override
	public UserAwardMapExecution getUserAwardMapById(long userAwardMapId) {
		if (userAwardMapId <= 0) {
			return new UserAwardMapExecution(UserAwardMapStateEnum.EMPTY);
		}
		return new UserAwardMapExecution(UserAwardMapStateEnum.SUCCESS, userAwardMapDao.queryUserAwardMapById(userAwardMapId));
	}


	@Override
	@Transactional
	public UserAwardMapExecution addUserAwardMap(UserAwardMap userAwardMap) {
		if (userAwardMap == null || userAwardMap.getUser() == null || userAwardMap.getUser().getUserId() == null
				|| userAwardMap.getShop() == null || userAwardMap.getShop().getShopId() == null) {
			return new UserAwardMapExecution(UserAwardMapStateEnum.EMPTY);
		}
		userAwardMap.setCreateTime(new Date());
		userAwardMap.setUsedStatus(0);
		//若用户兑换的奖品实体所需要的积分大于0, 则扣除tb_user_shop_map表中对应的用户积分字段
		if (userAwardMap.getPoint() != null && userAwardMap.getPoint() > 0) {// 若该奖品需要消耗积分，则将tb_user_shop_map对应的用户积分抵扣
			// 根据用户Id和店铺Id获取该用户在店铺的积分
			UserShopMap userShopMap = userShopMapDao.queryUserShopMap(userAwardMap.getUser().getUserId(), userAwardMap.getShop().getShopId());
			// 判断该用户在店铺里是否有积分, 且保证积分足够兑换
            if (userShopMap != null && userShopMap.getPoint() <= userAwardMap.getPoint()) {
                throw new UserAwardMapOperationException("在本店铺没有积分，无法对换奖品");
            }
            // 若有积分，必须确保店铺积分大于本次要兑换奖品需要的积分
            userShopMap.setPoint(userShopMap.getPoint() - userAwardMap.getPoint());
            int effectedNum = userShopMapDao.updateUserShopMapPoint(userShopMap);
            if (effectedNum <= 0) {
                logger.error("更新用户对应店铺的积分信息失败, 返回0条变更");
                throw new UserAwardMapOperationException("领取奖励失败");
            }
            // 插入礼品兑换信息
            effectedNum = userAwardMapDao.insertUserAwardMap(userAwardMap);
            if (effectedNum <= 0) {
                logger.error("更新用户兑换的奖品信息失败, 返回0条变更");
                throw new UserAwardMapOperationException("领取奖励失败");
            }
		} else {
            return new UserAwardMapExecution(UserAwardMapStateEnum.EMPTY);
        }
        return new UserAwardMapExecution(UserAwardMapStateEnum.SUCCESS, userAwardMap);
	}

	@Override
	@Transactional
	public UserAwardMapExecution modifyUserAwardMap(UserAwardMap userAwardMap) {
		if (userAwardMap == null || userAwardMap.getUserAwardId() == null || userAwardMap.getUsedStatus() == null) {
			return new UserAwardMapExecution(UserAwardMapStateEnum.EMPTY);
		}
        int effectedNum = userAwardMapDao.updateUserAwardMap(userAwardMap);
        if (effectedNum <= 0) {
            logger.error("更新用户兑换的奖品实例失败, 返回0条变更");
            return new UserAwardMapExecution(UserAwardMapStateEnum.INNER_ERROR);
        } else {
            return new UserAwardMapExecution(UserAwardMapStateEnum.SUCCESS, userAwardMap);
        }
	}
}
