package com.whl.o2o.dao;

import com.whl.o2o.entity.Shop;
import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.entity.UserShopMap;
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
public class UserShopMapDaoTest {
    @Autowired
    private UserShopMapDao userShopMapDao;

    @Test
    public void testInsert() {
        UserShopMap u = UserShopMap.builder().createTime(new Date()).point(5).
                shop(Shop.builder().shopId(1L).build()).
                user(UserInfo.builder().userId(1L).build()).build();
        userShopMapDao.insertUserShopMap(u);
    }

    @Test
    public void testQueryById() {
        userShopMapDao.queryUserShopMap(1L, 1L);
    }

    @Test
    public void testQueryByCondition() {
        UserShopMap u = UserShopMap.builder().user(UserInfo.builder().username("测试").build()).build();
        int count = userShopMapDao.queryUserShopMapCount(u);
        List<UserShopMap> list = userShopMapDao.queryUserShopMapList(u, 0, 5);
        System.out.println(count + " " + list.size());
    }

    @Test
    public void testUpdate() {
        userShopMapDao.updateUserShopMapPoint(UserShopMap.builder().user(UserInfo.builder().userId(1L).build()).
                shop(Shop.builder().shopId(1L).build()).point(10).build());
    }
}
