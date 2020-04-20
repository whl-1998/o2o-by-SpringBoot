package com.whl.o2o.dto;

import com.whl.o2o.entity.UserInfo;
import com.whl.o2o.enums.UserInfoStateEnum;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title: 封装执行后结果
 * @Description:
 */
@Data
public class UserInfoExecution {
	private int state;
	private String stateInfo;
	private int count;
	private UserInfo personInfo;
	private List<UserInfo> personInfoList;

	@Tolerate
	public UserInfoExecution() {
	}

	public UserInfoExecution(UserInfoStateEnum stateEnum) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}

	public UserInfoExecution(UserInfoStateEnum stateEnum, UserInfo personInfo) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.personInfo = personInfo;
	}

	public UserInfoExecution(UserInfoStateEnum stateEnum, List<UserInfo> personInfoList) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.personInfoList = personInfoList;
	}
}