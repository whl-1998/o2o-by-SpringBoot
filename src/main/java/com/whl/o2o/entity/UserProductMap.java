package com.whl.o2o.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.experimental.Tolerate;

import java.util.Date;

/**
 * @author whl
 * @version V1.0
 * @Title: 顾客消费的商品映射
 * @Description:
 */
@Data
@Builder
public class UserProductMap {
	private Long userProductId;
	private Date createTime;
	private Integer point;
	private UserInfo user;
	private Product product;
	private Shop shop;
	private UserInfo operator;

	@Tolerate
	public UserProductMap() {
	}
}
