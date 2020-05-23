package com.whl.o2o.service;

import com.whl.o2o.dto.ProductCarExecution;
import com.whl.o2o.entity.Product;
import com.whl.o2o.entity.ProductCar;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface ProductCarService {
    ProductCarExecution addProductCar(ProductCar productCar);

    ProductCarExecution updateProductCar(ProductCar productCar);

    ProductCarExecution selectProductCar(ProductCar productCarCondition, int rowIndex, int pageSize);
}
