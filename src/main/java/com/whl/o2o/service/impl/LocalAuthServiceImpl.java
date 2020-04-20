package com.whl.o2o.service.impl;

import com.whl.o2o.dao.LocalAuthDao;
import com.whl.o2o.dto.LocalAuthExecution;
import com.whl.o2o.entity.LocalAuth;
import com.whl.o2o.enums.LocalAuthStateEnum;
import com.whl.o2o.exceptions.LocalAuthOperationException;
import com.whl.o2o.service.LocalAuthService;
import com.whl.o2o.util.MD5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Service
public class LocalAuthServiceImpl implements LocalAuthService {
    @Autowired
    LocalAuthDao localAuthDao;

    private final static Logger logger = LoggerFactory.getLogger(LocalAuthServiceImpl.class);

    @Override
    public LocalAuthExecution getLocalAuthByUserId(long userId) {
        if (userId <= 0) {
            return new LocalAuthExecution(LocalAuthStateEnum.EMPTY);
        }
        return new LocalAuthExecution(LocalAuthStateEnum.SUCCESS, localAuthDao.queryLocalAuthByUserId(userId));
    }

    @Override
    @Transactional
    public LocalAuthExecution bindLocalAuth(LocalAuth localAuth) throws LocalAuthOperationException {
        if (localAuth == null || localAuth.getPassword() == null || localAuth.getUsername() == null || localAuth.getUserInfo() == null) {
            return new LocalAuthExecution(LocalAuthStateEnum.EMPTY);
        }
        LocalAuth tempAuth = localAuthDao.queryLocalAuthByUserId(localAuth.getUserInfo().getUserId());
        if (tempAuth != null) {
            return new LocalAuthExecution(LocalAuthStateEnum.ONLY_ONE_ACCOUNT);
        }
        localAuth.setCreateTime(new Date());
        localAuth.setUpdateTime(new Date());
        localAuth.setPassword(MD5.getMd5(localAuth.getPassword()));
        int effectedNum = localAuthDao.insertLocalAuth(localAuth);
        if (effectedNum <= 0) {
            logger.error("插入localAuth失败, 返回0条变更");
            throw new LocalAuthOperationException("账号绑定失败");
        }
        return new LocalAuthExecution(LocalAuthStateEnum.SUCCESS, localAuth);
    }

    @Override
    @Transactional
    public LocalAuthExecution modifyLocalAuth(Long userId, String username, String password, String newPassword, Date updateTime) throws LocalAuthOperationException {
        if (userId == null || username == null || password == null || newPassword == null) {
            return new LocalAuthExecution(LocalAuthStateEnum.EMPTY);
        } else if (password.equals(newPassword)) {
            return new LocalAuthExecution(LocalAuthStateEnum.ERROR_NEW_PASSWORD);
        }
        int effectedNum = localAuthDao.updateLocalAuth(userId, username, MD5.getMd5(password), MD5.getMd5(newPassword), new Date());
        if (effectedNum <= 0) {
            logger.error("更新localAuth失败, 返回0条变更");
            throw new LocalAuthOperationException("更新密码失败");
        }
        return new LocalAuthExecution(LocalAuthStateEnum.SUCCESS);
    }

    @Override
    public LocalAuthExecution getLocalAuthByUsernameAndPwd(String username, String password) {
        if (username == null || password == null || username.equals("") || password.equals("")) {
            return new LocalAuthExecution(LocalAuthStateEnum.EMPTY);
        }
        return new LocalAuthExecution(LocalAuthStateEnum.SUCCESS, localAuthDao.queryLocalAuthByUserNameAndPwd(username, MD5.getMd5(password)));
    }
}
