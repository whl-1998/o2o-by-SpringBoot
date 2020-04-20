package com.whl.o2o.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Date;

/**
 * @author whl
 * @version V1.0
 * @Title: 奖品实体类
 * @Description:
 */
@Data
@Builder
public class Award {
	private Long awardId;
	private String awardName;
	private String awardDesc;
	private String awardImg;
	private Integer point;
	private Integer priority;
	private Date createTime;
	private Date updateTime;
	private Integer enableStatus;// 可用状态 0.不可用 1.可用
	// 属于哪个店铺
	private Long shopId;

	@Tolerate
	public Award() {
	}
}
