package com.whl.o2o.service;

import com.whl.o2o.dto.UserInfoExecution;
import com.whl.o2o.entity.UserInfo;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface UserInfoService {
    UserInfoExecution getUserInfoById(Long userId);

    UserInfoExecution getPersonInfoList(UserInfo userInfoCondition, int pageIndex, int pageSize);

    UserInfoExecution modifyUserInfo(UserInfo userInfo);
}
