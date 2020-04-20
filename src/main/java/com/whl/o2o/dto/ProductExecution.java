package com.whl.o2o.dto;

import com.whl.o2o.entity.Product;
import com.whl.o2o.enums.ProductStateEnum;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;

@Data
public class ProductExecution {
    private int state;
    private String stateInfo;
    private int count;
    private Product product;
    private List<Product> productList;

    @Tolerate
    public ProductExecution() {
    }

    public ProductExecution(ProductStateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    public ProductExecution(ProductStateEnum stateEnum, Product product) {
        this.stateInfo = stateEnum.getStateInfo();
        this.state = stateEnum.getState();
        this.product = product;
    }

    public ProductExecution(ProductStateEnum stateEnum, List<Product> productList) {
        this.stateInfo = stateEnum.getStateInfo();
        this.state = stateEnum.getState();
        this.productList = productList;
    }
}
