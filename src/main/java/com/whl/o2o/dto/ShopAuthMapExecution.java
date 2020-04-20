package com.whl.o2o.dto;

import java.util.List;
import com.whl.o2o.entity.ShopAuthMap;
import com.whl.o2o.enums.ShopAuthMapStateEnum;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
public class ShopAuthMapExecution {
	private int state;
	private String stateInfo;
	private Integer count;
	private ShopAuthMap shopAuthMap;
	private List<ShopAuthMap> shopAuthMapList;

	@Tolerate
	public ShopAuthMapExecution() {
	}

	public ShopAuthMapExecution(ShopAuthMapStateEnum stateEnum) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}

	public ShopAuthMapExecution(ShopAuthMapStateEnum stateEnum, ShopAuthMap shopAuthMap) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.shopAuthMap = shopAuthMap;
	}

	public ShopAuthMapExecution(ShopAuthMapStateEnum stateEnum, List<ShopAuthMap> shopAuthMapList) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.shopAuthMapList = shopAuthMapList;
	}
}

