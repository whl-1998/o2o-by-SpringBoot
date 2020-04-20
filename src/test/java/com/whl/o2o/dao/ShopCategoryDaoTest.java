package com.whl.o2o.dao;

import com.whl.o2o.entity.ShopCategory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
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
public class ShopCategoryDaoTest {
    @Autowired
    private ShopCategoryDao shopCategoryDao;

    @Test
    public void testInsert() {
        ShopCategory shopCategoryParent = ShopCategory.builder().
                shopCategoryName("testCtgr").
                createTime(new Date()).
                updateTime(new Date()).
                priority(100).
                shopCategoryDesc("desc").
                shopCategoryImg("img").build();
        shopCategoryDao.insertShopCategory(shopCategoryParent);

        ShopCategory shopCategoryChild = ShopCategory.builder().
                shopCategoryName("testCtgrChild").
                createTime(new Date()).
                updateTime(new Date()).
                priority(100).
                shopCategoryDesc("desc").
                shopCategoryImg("img").parent(shopCategoryParent).build();
        shopCategoryDao.insertShopCategory(shopCategoryChild);
    }

    @Test
    public void testUpdate() {
        ShopCategory shopCategoryParent = ShopCategory.builder().
                shopCategoryName("testCategory").
                shopCategoryId(20L).
                updateTime(new Date()).
                priority(100).
                shopCategoryDesc("desc").
                shopCategoryImg("img").build();
        shopCategoryDao.updateShopCategory(shopCategoryParent);
    }

    @Test
    public void testQueryById() {
        System.out.println(shopCategoryDao.queryShopCategoryById(20L).getShopCategoryName());
    }

    @Test
    public void testQueryByIds() {
        List<Long> list = new ArrayList<>();
        list.add(20L);
        list.add(21L);
        List<ShopCategory> shopCategoryList = shopCategoryDao.queryShopCategoryByIds(list);
        System.out.println(shopCategoryList.size());
    }

    @Test
    public void testQueryByCondition() {
        ShopCategory parent = ShopCategory.builder().shopCategoryId(1L).build();
        List<ShopCategory> shopCategoryList = shopCategoryDao.queryShopCategory(null);
        System.out.println(shopCategoryList.size());
    }

    @Test
    public void testDelete() {
        shopCategoryDao.deleteShopCategory(21L);
    }

    @Test
    public void testBatchDelete() {
        List<Long> list = new ArrayList<>();
        list.add(20L);
        shopCategoryDao.batchDeleteShopCategory(list);
    }
}
