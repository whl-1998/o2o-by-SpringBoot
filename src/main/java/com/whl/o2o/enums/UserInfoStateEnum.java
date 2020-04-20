package com.whl.o2o.enums;

import lombok.Getter;

@Getter
public enum UserInfoStateEnum {
	SUCCESS(1, "操作成功"),
	INNER_ERROR(-1001, "内部系统错误"),
	EMPTY(-1002, "空参数或错误的参数");

	private int state;
	private String stateInfo;

	UserInfoStateEnum(int state, String stateInfo) {
		this.state = state;
		this.stateInfo = stateInfo;
	}

	public static UserInfoStateEnum stateOf(int index) {
		for (UserInfoStateEnum state : values()) {
			if (state.getState() == index) {
				return state;
			}
		}
		return null;
	}
}