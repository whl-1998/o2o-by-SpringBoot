package com.whl.o2o.enums;

import lombok.Getter;

@Getter
public enum  ProductStateEnum {
    SUCCESS(1, "操作成功"),
    INNER_ERROR(-1001, "内部系统错误"),
    EMPTY(-1002, "空参数或错误的参数");

    private int state;
    private String stateInfo;

    ProductStateEnum(int state,String stateInfo){
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public static ProductStateEnum stateOf(int state){
        for(ProductStateEnum stateEnum:values()){
            if(stateEnum.getState()==state){
                return stateEnum;
            }
        }
        return null;
    }
}
