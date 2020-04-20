package com.whl.o2o.dao;

import com.whl.o2o.entity.Product;
import com.whl.o2o.entity.Shop;
import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.entity.UserProductMap;
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
public class UserProductMapDaoTest {
    @Autowired
    private UserProductMapDao userProductMapDao;

    @Test
    public void testInsert() {
        Product p = Product.builder().productId(1L).build();
        Shop s = Shop.builder().shopId(1L).build();
        UserProductMap productMap = UserProductMap.builder().
                product(p).createTime(new Date()).
                user(UserInfo.builder().userId(1L).build()).
                operator(UserInfo.builder().userId(1L).build()).point(5).shop(s).build();
        userProductMapDao.insertUserProductMap(productMap);
    }

    @Test
    public void testQuery() {
        UserProductMap productMap = UserProductMap.builder().
                user(UserInfo.builder().username("测试").build())
                .build();
        List<UserProductMap> list = userProductMapDao.queryUserProductMapList(productMap, 0 , 5);
        int count = userProductMapDao.queryUserProductMapCount(productMap);
        System.out.println(list.size() + " " + count);
    }
}
