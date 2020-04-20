package com.whl.o2o.dao;

import com.whl.o2o.entity.ProductImg;
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
public class ProductImgDaoTest {
    @Autowired
    private ProductImgDao productImgDao;

    @Test
    public void testBatchInsert() {
        List<ProductImg> list = new ArrayList<>();
        list.add(ProductImg.builder().createTime(new Date()).imgAddr("test").
                imgDesc("desc").priority(100).productId(1L).build());
        list.add(ProductImg.builder().createTime(new Date()).imgAddr("test2").
                imgDesc("desc").priority(100).productId(1L).build());
        productImgDao.batchInsertProductImg(list);
    }

    @Test
    public void testQuery() {
        List<ProductImg> list = productImgDao.queryProductImgList(1L);
        System.out.println(list.size());
    }

    @Test
    public void testBatchDel() {
        productImgDao.deleteProductImgByProductId(1L);
    }
}
