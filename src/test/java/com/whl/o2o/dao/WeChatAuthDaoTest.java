package com.whl.o2o.dao;


import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.entity.WeChatAuth;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WeChatAuthDaoTest {

    @Autowired
    private WeChatAuthDao weChatAuthDao;

    @Test
    public void testAInsertWeChatAuth(){
        WeChatAuth weChatAuth = new WeChatAuth();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(2L);
        weChatAuth.setUserInfo(userInfo);
        weChatAuth.setOpenId("ceshiopenId");
        weChatAuth.setCreateTime(new Date());
        int effectedNum = weChatAuthDao.insertWeChatAuth(weChatAuth);
        assertEquals(1,effectedNum);
    }

    @Test
    public void testBQueryWeChatAuth(){
        WeChatAuth weChatAuth = weChatAuthDao.queryWeChatInfoByOpenId("ceshiopenId");
        System.out.println(weChatAuth.getUserInfo().getUsername());
    }
}
