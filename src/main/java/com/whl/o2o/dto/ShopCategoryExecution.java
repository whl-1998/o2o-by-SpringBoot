package com.whl.o2o.dto;

import com.whl.o2o.entity.ShopCategory;
import com.whl.o2o.enums.ShopCategoryStateEnum;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;

@Data
public class ShopCategoryExecution {
	private int state;
	private String stateInfo;
	private ShopCategory shopCategory;
	private List<ShopCategory> shopCategoryList;

	@Tolerate
	public ShopCategoryExecution() {
	}

	public ShopCategoryExecution(ShopCategoryStateEnum stateEnum) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}

	public ShopCategoryExecution(ShopCategoryStateEnum stateEnum, ShopCategory shopCategory) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.shopCategory = shopCategory;
	}

	public ShopCategoryExecution(ShopCategoryStateEnum stateEnum, List<ShopCategory> shopCategoryList) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.shopCategoryList = shopCategoryList;
	}
}
