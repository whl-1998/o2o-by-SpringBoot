package com.whl.o2o.enums;

import lombok.Getter;

@Getter
public enum ShopStateEnum {
    CHECK(0, "审核中"),
    SUCCESS(1, "操作成功"),
    INNER_ERROR(-1001, "内部系统错误"),
    EMPTY(-1002, "空参数或错误的参数");

    private int state;
    private String stateInfo;

    ShopStateEnum(int state,String stateInfo){
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public static ShopStateEnum stateOf(int state){
        //遍历所有的枚举 如果存在符合的则返回
        for(ShopStateEnum stateEnum:values()){
            if(stateEnum.getState()==state){
                return stateEnum;
            }
        }
        return null;
    }
}
