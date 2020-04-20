package com.whl.o2o.dao;

import com.whl.o2o.entity.Product;
import com.whl.o2o.entity.ProductSellDaily;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductSellDailyDaoTest {
    @Autowired
    private ProductSellDailyDao productSellDailyDao;

    @Test
    public void testInsert() {
        productSellDailyDao.insertProductSellDaily();
    }

    @Test
    public void testQuery() {
        List<ProductSellDaily> list = productSellDailyDao.queryProductSellDailyList(ProductSellDaily.builder().
                product(Product.builder().productName("蓝莓").build()).build(), null, null);
        System.out.println(list.size());
    }
}
