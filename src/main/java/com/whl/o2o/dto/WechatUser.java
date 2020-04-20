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
public class WechatUser {
    @JsonProperty("openid")
    private String openId;// openId,标识该公众号下面的该用户的唯一Id

    @JsonProperty("nickname")
    private String nickName;// 用户昵称

    @JsonProperty("sex")
    private int sex;// 性别

    @JsonProperty("province")
    private String province;// 省份

    @JsonProperty("city")
    private String city;// 城市

    @JsonProperty("country")
    private String country;// 区

    @JsonProperty("headimgurl")
    private String headimgurl;// 头像图片地址

    @JsonProperty("language")
    private String language;// 语言

    @JsonProperty("privilege")
    private String[] privilege;// 用户权限
}
