package com.whl.o2o.dao;

import com.whl.o2o.entity.Product;
import com.whl.o2o.entity.ProductCategory;
import com.whl.o2o.entity.Shop;
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
public class ProductDaoTest {
    @Autowired
    private ProductDao productDao;

    @Test
    public void testAInsertProduct(){
        Shop shop = Shop.builder().shopId(1L).build();
        ProductCategory productCategory = ProductCategory.builder().productCategoryId(3L).build();
        Product product1 = Product.builder().shop(shop).productCategory(productCategory).imgAddr("test").
                productName("testname").productDesc("tedesc").priority(100).point(100).normalPrice("12").promotionPrice("12").enableStatus(1).createTime(new Date()).
                updateTime(new Date()).build();
        int effectedNum = productDao.insertProduct(product1);
        assertEquals(1,effectedNum);
    }

    @Test
    public void testQuery(){
        Product productCondition = Product.builder().productName("芒果").build();
        List<Product> productList = productDao.queryProductList(productCondition, 0, 5);
        System.out.println(productList.size());
    }

    @Test
    public void testQueryById(){
        Product p = productDao.queryProductById(34L);
        System.out.println(p.getProductName());
    }

    @Test
    public void testUpdate() {
        Product product = Product.builder().productId(33L).shop(Shop.builder().shopId(1L).build()).normalPrice("50").point(100).build();
        int effectedNum = productDao.updateProduct(product);
        assertEquals(1, effectedNum);
    }


    @Test
    public void testUpdateProductCategoryToNull(){
        int effectedNum = productDao.updateProductCategoryToNull(3L);
        assertEquals(2, effectedNum);
    }

    @Test
    public void testDel() {
        productDao.deleteProduct(33L, 1L);
    }
}
