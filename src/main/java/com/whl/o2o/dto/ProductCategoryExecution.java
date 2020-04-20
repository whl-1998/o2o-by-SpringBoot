package com.whl.o2o.dto;

import com.whl.o2o.entity.ProductCategory;
import com.whl.o2o.enums.ProductCategoryStateEnum;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;

@Data
public class ProductCategoryExecution {
    private int state;
    private String stateInfo;
    private int count;
    private ProductCategory productCategory;
    private List<ProductCategory> productCategoryList;

    @Tolerate
    public ProductCategoryExecution() {
    }

    //店铺操作失败的时候使用的构造器,只返回结果状态和标识
    public ProductCategoryExecution(ProductCategoryStateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    //成功的构造器  增删改
    public ProductCategoryExecution(ProductCategoryStateEnum stateEnum, ProductCategory productCategory) {
        this.stateInfo = stateEnum.getStateInfo();
        this.state = stateEnum.getState();
        this.productCategory = productCategory;
    }

    //成功的构造器  查询
    public ProductCategoryExecution(ProductCategoryStateEnum stateEnum, List<ProductCategory> productCategoryList) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.productCategoryList = productCategoryList;
    }
}
