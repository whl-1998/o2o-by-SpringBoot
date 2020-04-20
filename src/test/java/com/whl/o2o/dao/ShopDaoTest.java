package com.whl.o2o.dao;

import com.whl.o2o.entity.Area;
import com.whl.o2o.entity.Shop;
import com.whl.o2o.entity.ShopCategory;
import com.whl.o2o.entity.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopDaoTest {
    @Autowired
    private ShopDao shopDao;

    @Test
    public void testInsertShop(){
        UserInfo userInfo = UserInfo.builder().userId(1L).build();
        Area area = Area.builder().areaId(2).build();
        ShopCategory shopCategory = ShopCategory.builder().shopCategoryId(1L).build();
        Shop shop = Shop.builder().userInfo(userInfo).area(area).shopCategory(shopCategory).
                shopName("test2").shopAddr("testadd").shopImg("testImg").phone("15542381883").shopDesc("desc").
                createTime(new Date()).updateTime(new Date()).enableStatus(1).advice("ad").priority(100).build();
        int effectedNum = shopDao.insertShop(shop);
        assertEquals(1, effectedNum);
    }

    @Test
    public void testUpdateShop(){
        Area area = Area.builder().areaId(1).build();
        ShopCategory shopCategory = ShopCategory.builder().shopCategoryId(1L).build();
        Shop shop = Shop.builder().shopId(35L).area(area).shopCategory(shopCategory).
                shopName("testddd22").shopAddr("testadd").shopImg("testImg").phone("15542381883").shopDesc("desc").
                updateTime(new Date()).enableStatus(1).advice("ad").priority(100).build();
        int effectedNum = shopDao.updateShop(shop);
        assertEquals(1, effectedNum);
    }

    @Test
    public void testQueryById(){
        Shop shop = shopDao.queryByShopId(35L);
        System.out.println(shop.getShopName());
    }

    @Test
    public void testQueryCount() {
        Area area = Area.builder().areaId(3).build();
        ShopCategory shopCategory = ShopCategory.builder().build();
        Shop shopCondition = Shop.builder().area(area).shopCategory(shopCategory).build();
        List<Shop> shopList = shopDao.queryShopList(shopCondition, 0, 5);
        System.out.println(shopList.size());
        System.out.println(shopDao.queryShopCount(shopCondition));
    }
}
