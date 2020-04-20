package com.whl.o2o.service.impl;

import com.whl.o2o.dao.UserInfoDao;
import com.whl.o2o.dto.UserInfoExecution;
import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.enums.UserInfoStateEnum;
import com.whl.o2o.service.UserInfoService;
import com.whl.o2o.util.PageCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.nio.cs.US_ASCII;

import java.util.Date;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoDao userInfoDao;

    private final static Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    @Override
    public UserInfoExecution getUserInfoById(Long userId) {
        if (userId <= 0) {
            return new UserInfoExecution(UserInfoStateEnum.EMPTY);
        }
        return new UserInfoExecution(UserInfoStateEnum.SUCCESS, userInfoDao.queryUserInfoById(userId));
    }

    @Override
    public UserInfoExecution getPersonInfoList(UserInfo userInfoCondition, int pageIndex, int pageSize) {
        if (userInfoCondition == null || pageIndex <= 0 || pageSize <= 0) {
            return new UserInfoExecution(UserInfoStateEnum.EMPTY);
        }
        int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
        List<UserInfo> personInfoList = userInfoDao.queryUserInfoList(userInfoCondition, rowIndex, pageSize);
        int count = userInfoDao.queryUserInfoByCount(userInfoCondition);
        UserInfoExecution se = new UserInfoExecution();
        if (personInfoList.size() == count) {
            se.setPersonInfoList(personInfoList);
            se.setCount(count);
        } else {
            return new UserInfoExecution(UserInfoStateEnum.INNER_ERROR);
        }
        return se;
    }

    @Override
    @Transactional
    public UserInfoExecution modifyUserInfo(UserInfo userInfo) {
        if (userInfo == null || userInfo.getUserId() == null) {
            return new UserInfoExecution(UserInfoStateEnum.EMPTY);
        }
        userInfo.setUpdateTime(new Date());
        int effectedNum = userInfoDao.updateUserInfo(userInfo);
        if (effectedNum <= 0) {
            logger.error("更新用户信息失败, 返回0条变更");
            return new UserInfoExecution(UserInfoStateEnum.INNER_ERROR);
        }
        return new UserInfoExecution(UserInfoStateEnum.SUCCESS, userInfo);
    }
}
