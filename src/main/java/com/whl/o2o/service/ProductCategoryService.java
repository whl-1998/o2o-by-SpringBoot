package com.whl.o2o.service;

import com.whl.o2o.dto.ProductCategoryExecution;
import com.whl.o2o.dto.ProductExecution;
import com.whl.o2o.entity.ProductCategory;
import com.whl.o2o.exceptions.ProductCategoryOperationException;

import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface ProductCategoryService {
    ProductCategoryExecution getProductCategoryList(Long shopId);

    ProductCategoryExecution batchAddProductCategory(List<ProductCategory> productCategoryList);

    ProductCategoryExecution deleteProductCategory(Long productCategoryId, Long shopId);
}
