package com.whl.o2o.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Date;

/**
 * @author whl
 * @version V1.0
 * @Title: 顾客对应店铺的积分映射
 * @Description:
 */
@Data
@Builder
public class UserShopMap {
	private Long userShopId;
	private Date createTime;
	private Integer point;
	private UserInfo user;
	private Shop shop;

	@Tolerate
	public UserShopMap() {
	}
}
