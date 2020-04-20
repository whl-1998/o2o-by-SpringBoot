package com.whl.o2o.enums;
import lombok.Getter;

@Getter
public enum LocalAuthStateEnum {
	SUCCESS(1, "操作成功"),
	INNER_ERROR(-1001, "内部系统错误"),
	EMPTY(-1002, "空参数或错误的参数"),
	LOGIN_FAIL(-1003, "密码或帐号输入有误"),
	ONLY_ONE_ACCOUNT(-1004, "最多只能绑定一个本地帐号"),
	ERROR_NEW_PASSWORD(-1005, "新密码输入有误");

	private int state;
	private String stateInfo;

	LocalAuthStateEnum(int state, String stateInfo) {
		this.state = state;
		this.stateInfo = stateInfo;
	}

	public static LocalAuthStateEnum stateOf(int index) {
		for (LocalAuthStateEnum state : values()) {
			if (state.getState() == index) {
				return state;
			}
		}
		return null;
	}
}
