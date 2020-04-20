package com.whl.o2o.dao;

import com.whl.o2o.entity.WeChatAuth;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface WeChatAuthDao {
    WeChatAuth queryWeChatInfoByOpenId(String openId);

    int insertWeChatAuth(WeChatAuth weChatAuth);
}
