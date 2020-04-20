package com.whl.o2o.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Data
public class WeChatAuth {
    private Long wechatAuthId;
    private String openId;
    private Date createTime;
    private UserInfo userInfo;
}
