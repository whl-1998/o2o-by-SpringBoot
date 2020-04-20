package com.whl.o2o.dao;

import com.whl.o2o.entity.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserInfoDaoTest {
    @Autowired
    private UserInfoDao userInfoDao;

    @Test
    public void testQueryById() {
        UserInfo u = userInfoDao.queryUserInfoById(3L);
        System.out.println(u.getUsername());
    }

    @Test
    public void testQueryByCondition() {
        List<UserInfo> list = userInfoDao.queryUserInfoList(UserInfo.builder().username("测试").enableStatus(1).build(), 0, 5);
        System.out.println(list.size());
        int count = userInfoDao.queryUserInfoByCount(UserInfo.builder().username("测试").enableStatus(1).build());
        System.out.println(count);
    }

    @Test
    public void testInsert() {
        userInfoDao.insertUserInfo(UserInfo.builder().username("汪1998").enableStatus(1).createTime(new Date()).gender("男").profileImg("test").updateTime(new Date()).userType(3).build());
    }

    @Test
    public void testUpdate() {
        userInfoDao.updateUserInfo(UserInfo.builder().userId(4L).email("test").updateTime(new Date()).build());
    }
}
