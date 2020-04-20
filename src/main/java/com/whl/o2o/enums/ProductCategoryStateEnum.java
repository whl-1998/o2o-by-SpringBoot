package com.whl.o2o.enums;

import lombok.Getter;

@Getter
public enum ProductCategoryStateEnum {
    SUCCESS(1, "操作成功"),
    INNER_ERROR(-1001, "内部系统错误"),
    EMPTY(-1002, "空参数或错误的参数");

    private int state;
    private String stateInfo;

    ProductCategoryStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public static ProductCategoryStateEnum stateOf(int state) {
        for(ProductCategoryStateEnum stateEnum:values()){
            if(stateEnum.getState() == state){
                return stateEnum;
            }
        }
        return null;
    }
}
