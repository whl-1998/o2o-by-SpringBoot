package com.whl.o2o.dao;

import com.whl.o2o.entity.LocalAuth;
import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.util.MD5;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;


/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LocalAuthDaoTest {
    @Autowired
    private LocalAuthDao localAuthDao;

    @Test
    public void testQueryByUsernameAndPwd() {
        LocalAuth l = localAuthDao.queryLocalAuthByUserNameAndPwd("w1998000", "w1998");
        System.out.println(l.getUsername());
    }

    @Test
    public void testQueryById() {
        LocalAuth l = localAuthDao.queryLocalAuthByUserId(1L);
        System.out.println(l.getUsername());
    }

    @Test
    public void testInsert() {
        localAuthDao.insertLocalAuth(LocalAuth.builder().createTime(new Date()).updateTime(new Date()).
                username("w1998000").password("w1998").userInfo(UserInfo.builder().userId(1L).build()).build());
    }

    @Test
    public void testUpdate() {
        localAuthDao.updateLocalAuth(1L, "w1998000", "w1998", "w1997", new Date());
    }
}
