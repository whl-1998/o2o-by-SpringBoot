package com.whl.o2o.dao;

import com.whl.o2o.entity.Product;
import com.whl.o2o.entity.ProductCar;
import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.util.HttpServletRequestUtil;
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
public class ProductCarDaoTest {
    @Autowired
    private ProductCarDao productCarDao;

    @Test
    public void testSelect() {
        ProductCar condition = new ProductCar();
        Product product = Product.builder().productName("奥").build();
        UserInfo user = UserInfo.builder().userId(6L).build();
        condition.setProduct(product);
        condition.setUserInfo(user);
        List<ProductCar> productCars = productCarDao.queryProductCarList(condition, 0, 2);
        System.out.println(productCars.size());
    }

    @Test
    public void testInsert() {
        ProductCar c = new ProductCar();
        c.setCreateTime(new Date());
        c.setUpdateTime(new Date());
        c.setStatus(0);
        c.setProduct(Product.builder().productId(3L).build());
        c.setUserInfo(UserInfo.builder().userId(1L).build());
        int res = productCarDao.insertProductCar(c);
        System.out.println(res);
    }

    @Test
    public void testUpdate() {
        ProductCar c = new ProductCar();
        c.setProductCarId(1L);
        c.setUpdateTime(new Date());
        c.setStatus(1);
        int res = productCarDao.updateProductCar(c);
        System.out.println(res);
    }

    @Test
    public void testDel() {
        productCarDao.deleteProductCar(4L);
    }
}
