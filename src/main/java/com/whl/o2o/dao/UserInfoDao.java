package com.whl.o2o.dao;

import com.whl.o2o.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface UserInfoDao {

    List<UserInfo> queryUserInfoList(@Param("userInfoCondition") UserInfo userInfoCondition, @Param("rowIndex") int rowIndex, @Param("pageSize") int pageSize);

    int queryUserInfoByCount(@Param("userInfoCondition") UserInfo userInfoCondition);

    UserInfo queryUserInfoById(long userId);

    int insertUserInfo(UserInfo userInfo);

    int updateUserInfo(UserInfo userInfo);
}
