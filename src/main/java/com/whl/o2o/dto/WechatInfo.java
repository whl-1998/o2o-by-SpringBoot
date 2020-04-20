package com.whl.o2o.dto;

import lombok.Data;

/**
 * @author whl
 * @version V1.0
 * @Title: 用来接收平台二维码的信息
 * @Description:
 */
@Data
public class WechatInfo {
	private Long customerId;
	private Long productId;
	private Long userAwardId;
	private Long createTime;
	private Long shopId;
}
