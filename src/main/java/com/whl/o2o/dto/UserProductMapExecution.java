package com.whl.o2o.dto;

import java.util.List;

import com.whl.o2o.entity.UserProductMap;
import com.whl.o2o.enums.UserProductMapStateEnum;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
public class UserProductMapExecution {
	private int state;
	private String stateInfo;
	private Integer count;
	private UserProductMap userProductMap;
	private List<UserProductMap> userProductMapList;

	@Tolerate
	public UserProductMapExecution() {
	}

	public UserProductMapExecution(UserProductMapStateEnum stateEnum) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}

	public UserProductMapExecution(UserProductMapStateEnum stateEnum, UserProductMap userProductMap) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.userProductMap = userProductMap;
	}

	public UserProductMapExecution(UserProductMapStateEnum stateEnum, List<UserProductMap> userProductMapList) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.userProductMapList = userProductMapList;
	}
}
