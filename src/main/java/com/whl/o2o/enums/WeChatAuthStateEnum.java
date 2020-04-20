package com.whl.o2o.enums;

import lombok.Getter;

@Getter
public enum  WeChatAuthStateEnum {
    SUCCESS(1, "操作成功"),
    INNER_ERROR(-1001, "内部系统错误"),
    EMPTY(-1002, "空参数或错误的参数"),
    OPEN_ID_FAIL(-1003, "openId错误");

    private int state;
    private String stateInfo;

    WeChatAuthStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public static WeChatAuthStateEnum stateOf(int state) {
        for (WeChatAuthStateEnum stateEnum : values()) {
            if (stateEnum.getState() == state) {
                return stateEnum;
            }
        }
        return null;
    }
}
