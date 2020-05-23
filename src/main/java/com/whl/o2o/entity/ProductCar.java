package com.whl.o2o.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author whl
 * @version V1.0
 * @Title: 购物车
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCar {
    private Long productCarId;
    private UserInfo userInfo;
    private Product product;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
