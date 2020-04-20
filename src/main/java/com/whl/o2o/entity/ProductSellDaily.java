package com.whl.o2o.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Date;

/**
 * @author whl
 * @version V1.0
 * @Title: 商品日销量实体类
 * @Description:
 */
@Data
@Builder
public class ProductSellDaily {
	private Long productSellDailyId;
	private Date createTime;
	private Integer total;// 销量
	private Product product;// 关联的商品
	private Shop shop;// 关联的店铺

	@Tolerate
	public ProductSellDaily() {
	}
}
