package com.whl.o2o.dto;

import java.util.List;

import com.whl.o2o.entity.UserShopMap;
import com.whl.o2o.enums.UserShopMapStateEnum;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
public class UserShopMapExecution {
	private int state;
	private String stateInfo;
	private Integer count;
	private UserShopMap userShopMap;
	private List<UserShopMap> userShopMapList;

	@Tolerate
	public UserShopMapExecution() {
	}

	public UserShopMapExecution(UserShopMapStateEnum stateEnum) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}

	public UserShopMapExecution(UserShopMapStateEnum stateEnum, UserShopMap userShopMap) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.userShopMap = userShopMap;
	}

	public UserShopMapExecution(UserShopMapStateEnum stateEnum, List<UserShopMap> userShopMapList) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.userShopMapList = userShopMapList;
	}
}
