package com.whl.o2o.dto;

import com.whl.o2o.entity.LocalAuth;
import com.whl.o2o.entity.ProductCar;
import com.whl.o2o.enums.LocalAuthStateEnum;
import com.whl.o2o.enums.ProductCarStateEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Tolerate;

import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Data
public class ProductCarExecution {
    private int state;
    private String stateInfo;
    private int count;
    private ProductCar productCar;
    private List<ProductCar> productCarList;

    public ProductCarExecution(ProductCarStateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    public ProductCarExecution(ProductCarStateEnum stateEnum, ProductCar productCar) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.productCar = productCar;
    }

    public ProductCarExecution(ProductCarStateEnum stateEnum, List<ProductCar> productCarList) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.productCarList = productCarList;
    }
}
