package com.whl.o2o.service;

import com.whl.o2o.dto.UserProductMapExecution;
import com.whl.o2o.entity.UserProductMap;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserProductMapServiceTest {
    @Autowired
    private UserProductMapService userProductMapService;

    public void testAddUserProductMap() {
        UserProductMap m = new UserProductMap();
//        m.setProduct();
//        userProductMapService.addUserProductMap()
    }
}
