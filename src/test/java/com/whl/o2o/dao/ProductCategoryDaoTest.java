package com.whl.o2o.dao;

import com.whl.o2o.entity.Product;
import com.whl.o2o.entity.ProductCategory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductCategoryDaoTest {
    @Autowired
    private ProductCategoryDao productCategoryDao;

    @Test
    public void testBatchInsert() {
        ProductCategory pc1 = ProductCategory.builder().createTime(new Date()).priority(100).productCategoryName("蔬菜").shopId(1L).build();
        ProductCategory pc2 = ProductCategory.builder().createTime(new Date()).priority(100).productCategoryName("TEST").shopId(1L).build();
        List<ProductCategory> list = new ArrayList<>();
        list.add(pc1);
        list.add(pc2);
        productCategoryDao.batchInsertProductCategory(list);
    }

    @Test
    public void testQuery() {
        List<ProductCategory> list = productCategoryDao.queryProductCategoryList(1L);
        System.out.println(list.size());
    }

    @Test
    public void testBatchDel() {
        productCategoryDao.deleteProductCategory(21L, 1L);
    }
}
