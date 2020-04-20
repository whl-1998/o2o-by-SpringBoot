package com.whl.o2o.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Date;

/**
 * @author whl
 * @version V1.0
 * @Title: 用户兑换过的奖品实体类
 * @Description:
 */
@Data
@Builder
public class UserAwardMap {
	private Long userAwardId;// 主键Id
	private Date createTime;
	private Integer usedStatus;// 使用状态0.未兑换 1.已兑换
	private Integer point;// 领取奖品所消耗的积分
	private UserInfo user;// 领取奖品的用户
	private Award award;// 奖品
	private Shop shop;// 该奖品关联的店铺
	private UserInfo operator;// 兑换奖品的操作员

	@Tolerate
	public UserAwardMap() {
	}
}
