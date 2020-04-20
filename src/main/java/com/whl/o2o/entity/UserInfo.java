package com.whl.o2o.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Date;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Data
@Builder
public class UserInfo {
    private Long userId;
    private String username;
    private String profileImg;//头像
    private String email;
    private String gender;
    private Integer enableStatus;//判断是否能正常登陆 0：禁止使用 1：正常
    private Integer userType;//区分用户属性 1：顾客 2：店家 3：超级管理员
    private Date createTime;//创建时间
    private Date updateTime;//更新时间

    @Tolerate
    public UserInfo() {
    }
}
