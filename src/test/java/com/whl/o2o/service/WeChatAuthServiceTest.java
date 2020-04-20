package com.whl.o2o.service;

import com.whl.o2o.dto.WeChatAuthExecution;
import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.entity.WeChatAuth;
import com.whl.o2o.enums.WeChatAuthStateEnum;
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
public class WeChatAuthServiceTest {
    @Autowired
    private WeChatAuthService weChatAuthService;

    @Test
    public void testRegister(){
        WeChatAuth weChatAuth = new WeChatAuth();
        UserInfo userInfo = new UserInfo();
        String openId = "daige1998";
        //给wx账号设置用户信息 但不设置用户id,这是希望wx账号自动创建用户信息
        userInfo.setCreateTime(new Date());
        userInfo.setUsername("WhlAJJJ");
        userInfo.setUserType(1);
        weChatAuth.setUserInfo(userInfo);
        weChatAuth.setOpenId(openId);
        weChatAuth.setCreateTime(new Date());
        WeChatAuthExecution wae = weChatAuthService.register(weChatAuth);
        assertEquals(WeChatAuthStateEnum.SUCCESS.getState(),wae.getState());

        //通过openId找到新增的wechatAuth
        weChatAuth = weChatAuthService.getWeChatAuthByOpenId(openId);
        System.out.println(weChatAuth.getUserInfo().getUsername());


    }
}
