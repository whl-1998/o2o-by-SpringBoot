package com.whl.o2o.service;


import com.whl.o2o.dto.ImageHolder;
import com.whl.o2o.dto.ProductExecution;
import com.whl.o2o.entity.Product;
import com.whl.o2o.exceptions.ProductOperationException;

import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface ProductService {
    ProductExecution addProduct(Product product, ImageHolder imageHolder, List<ImageHolder> imageHolderList);

    ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize);

    ProductExecution getProductById(long productId);

    ProductExecution modifyProduct(Product product, ImageHolder imageHolder, List<ImageHolder> imageHolderList);
}
