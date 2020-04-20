package com.whl.o2o.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Date;

/**
 * @author whl
 * @version V1.0
 * @Title:商铺实体类
 * @Description:
 */
@Data
@Builder
public class Shop {
    private Long shopId;
    private String shopName;
    private String shopDesc;
    private String shopAddr;
    private String phone;
    private String shopImg;
    private Integer priority;
    private Date createTime;
    private Date updateTime;
    private Integer enableStatus;//商铺状态 -1：不可用 0：审核中 1：可用 ，审核中是可以发布商品信息的
    private String advice;
    private Area area;
    private UserInfo userInfo;
    private ShopCategory shopCategory;

    @Tolerate
    public Shop() {
    }
}
