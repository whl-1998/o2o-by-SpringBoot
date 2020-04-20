package com.whl.o2o.service;

import com.whl.o2o.dto.WeChatAuthExecution;
import com.whl.o2o.entity.WeChatAuth;
import com.whl.o2o.exceptions.WeChatAuthOperationException;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface WeChatAuthService {
    WeChatAuth getWeChatAuthByOpenId(String openId);

    WeChatAuthExecution register(WeChatAuth weChatAuth) throws WeChatAuthOperationException;
}
