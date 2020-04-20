package com.whl.o2o.dao;

import com.whl.o2o.entity.LocalAuth;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface LocalAuthDao {
    LocalAuth queryLocalAuthByUserNameAndPwd(@Param("username") String username, @Param("password") String password);

    LocalAuth queryLocalAuthByUserId(@Param("userId") long userId);

    int insertLocalAuth(LocalAuth localAuth);

    int updateLocalAuth(@Param("userId") long userId,
                        @Param("username") String username,
                        @Param("password") String password,
                        @Param("newPassword") String newPassword,
                        @Param("updateTime") Date updateTime);
}
