package com.whl.o2o.dto;

import java.util.List;
import com.whl.o2o.entity.UserAwardMap;
import com.whl.o2o.enums.UserAwardMapStateEnum;
import lombok.Data;
import lombok.experimental.Tolerate;

@Data
public class UserAwardMapExecution {
	private int state;
	private String stateInfo;
	private Integer count;
	private UserAwardMap userAwardMap;
	private List<UserAwardMap> userAwardMapList;

	@Tolerate
	public UserAwardMapExecution() {
	}

	public UserAwardMapExecution(UserAwardMapStateEnum stateEnum) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}

	public UserAwardMapExecution(UserAwardMapStateEnum stateEnum, UserAwardMap userAwardMap) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.userAwardMap = userAwardMap;
	}

	public UserAwardMapExecution(UserAwardMapStateEnum stateEnum, List<UserAwardMap> userAwardMapList) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.userAwardMapList = userAwardMapList;
	}
}
