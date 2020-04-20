package com.whl.o2o.dao;

import com.whl.o2o.entity.Shop;
import com.whl.o2o.entity.ShopAuthMap;
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
public class ShopAuthDaoTest {
    @Autowired
    ShopAuthMapDao shopAuthMapDao;

    @Test
    public void testInsert() {
        ShopAuthMap s = ShopAuthMap.builder().createTime(new Date()).enableStatus(1).title("CTO").updateTime(new Date()).
                titleFlag(2).employee(UserInfo.builder().userId(1L).build()).shop(Shop.builder().shopId(1L).build()).build();
        shopAuthMapDao.insertShopAuthMap(s);
    }

    @Test
    public void testUpdate() {
        ShopAuthMap s = ShopAuthMap.builder().shopAuthId(3L).title("CEO").updateTime(new Date()).build();
        shopAuthMapDao.updateShopAuthMap(s);
    }

    @Test
    public void testQueryById() {
        ShopAuthMap s = shopAuthMapDao.queryShopAuthMapById(3L);
        System.out.println(s.getTitle());
    }

    @Test
    public void testQueryByShopId() {
        int count = shopAuthMapDao.queryShopAuthCountByShopId(1L);
        System.out.println(count);
        List<ShopAuthMap> shopAuthMaps = shopAuthMapDao.queryShopAuthMapListByShopId(1L, 0, 5);
        System.out.println(count + " " + shopAuthMaps.size());
    }

    @Test
    public void testDel() {
        shopAuthMapDao.deleteShopAuthMap(3l);
    }

}
