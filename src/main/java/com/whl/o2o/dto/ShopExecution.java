package com.whl.o2o.dto;

import com.whl.o2o.entity.Shop;
import com.whl.o2o.enums.ShopStateEnum;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Data
public class ShopExecution {
    private int state;
    private String stateInfo;
    private int count;
    private Shop shop;
    private List<Shop> shopList;

    @Tolerate
    public ShopExecution() {
    }

    //店铺操作失败的时候使用的构造器,只返回结果状态和标识
    public ShopExecution(ShopStateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    //成功的构造器  增删改
    public ShopExecution(ShopStateEnum stateEnum, Shop shop){
        this.stateInfo = stateEnum.getStateInfo();
        this.state = stateEnum.getState();
        this.shop = shop;
    }

    //成功的构造器  查询
    public ShopExecution(ShopStateEnum stateEnum,List<Shop> shopList){
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.shopList = shopList;
    }
}
