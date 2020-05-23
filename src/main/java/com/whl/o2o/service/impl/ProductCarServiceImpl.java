package com.whl.o2o.service.impl;

import com.whl.o2o.dao.ProductCarDao;
import com.whl.o2o.dto.ProductCarExecution;
import com.whl.o2o.dto.ProductExecution;
import com.whl.o2o.entity.ProductCar;
import com.whl.o2o.enums.ProductCarStateEnum;
import com.whl.o2o.enums.ProductStateEnum;
import com.whl.o2o.service.ProductCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Service
public class ProductCarServiceImpl implements ProductCarService {
    @Autowired
    private ProductCarDao productCarDao;

    @Override
    public ProductCarExecution addProductCar(ProductCar productCar) {
        if (productCar == null || productCar.getProduct() == null || productCar.getUserInfo() == null) {
            return new ProductCarExecution(ProductCarStateEnum.EMPTY);
        }
        productCar.setStatus(0); // 默认未支付
        productCar.setCreateTime(new Date());
        productCar.setUpdateTime(new Date());
        int effectedNum = productCarDao.insertProductCar(productCar);
        return effectedNum == 1 ? new ProductCarExecution(ProductCarStateEnum.SUCCESS) : new ProductCarExecution(ProductCarStateEnum.INNER_ERROR);
    }

    @Override
    public ProductCarExecution updateProductCar(ProductCar productCar) {
        if (productCar == null || productCar.getStatus() == null) {
            return new ProductCarExecution(ProductCarStateEnum.EMPTY);
        }
        int effectedNum = productCarDao.updateProductCar(productCar);
        return effectedNum == 1 ? new ProductCarExecution(ProductCarStateEnum.SUCCESS) : new ProductCarExecution(ProductCarStateEnum.INNER_ERROR);
    }

    @Override
    public ProductCarExecution selectProductCar(ProductCar productCarCondition, int rowIndex, int pageSize) {
        if (productCarCondition.getUserInfo() == null || productCarCondition.getUserInfo().getUserId()<= 0) {
            return new ProductCarExecution(ProductCarStateEnum.EMPTY);
        }
        List<ProductCar> productCarList = productCarDao.queryProductCarList(productCarCondition, rowIndex, pageSize);
        return new ProductCarExecution(ProductCarStateEnum.SUCCESS, productCarList);
    }
}
