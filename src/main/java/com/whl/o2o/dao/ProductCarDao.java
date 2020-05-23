package com.whl.o2o.dao;

import com.whl.o2o.entity.ProductCar;

import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface ProductCarDao {
    List<ProductCar> queryProductCarList(ProductCar productCarCondition, int rowIndex, int pageSize);

    int insertProductCar(ProductCar productCar);

    int deleteProductCar(long productCarId);

    int updateProductCar(ProductCar productCar);
}
