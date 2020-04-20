package com.whl.o2o.service;

import com.whl.o2o.dto.LocalAuthExecution;
import com.whl.o2o.entity.LocalAuth;
import com.whl.o2o.exceptions.LocalAuthOperationException;

import java.util.Date;

public interface LocalAuthService {
    LocalAuthExecution getLocalAuthByUserId(long userId);

    /**
     * 绑定微信, 生成平台专属账号
     * @param localAuth
     * @return
     * @throws LocalAuthOperationException
     */
    LocalAuthExecution bindLocalAuth(LocalAuth localAuth) throws LocalAuthOperationException;

    LocalAuthExecution modifyLocalAuth(Long userId, String username, String password, String newPassword, Date updateTime) throws LocalAuthOperationException;

    LocalAuthExecution getLocalAuthByUsernameAndPwd(String username, String password);
}
