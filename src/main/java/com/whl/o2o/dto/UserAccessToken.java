package com.whl.o2o.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Data
public class UserAccessToken {
    @JsonProperty("access_token")
    private String accessToken;// 获取到的凭证

    @JsonProperty("expires_in")
    private String expiresIn;// 凭证有效时间，单位：秒

    @JsonProperty("refresh_token")
    private String refreshToken;// 表示更新令牌，用来获取下一次的访问令牌，这里没太大用处

    @JsonProperty("openid")
    private String openId;// 该用户在此公众号下的身份标识，对于此微信号具有唯一性

    @JsonProperty("scope")
    private String scope;// 表示权限范围，这里可省略
}
