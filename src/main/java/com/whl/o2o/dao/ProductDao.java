package com.whl.o2o.dao;

import com.whl.o2o.entity.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductDao {
    List<Product> queryProductList(@Param("productCondition") Product productCondition, @Param("rowIndex") int rowIndex, @Param("pageSize") int pageSize);

    int queryProductCount(@Param("productCondition") Product productCondition);

    int insertProduct(Product product);

    int updateProduct(Product product);

    Product queryProductById(Long productId);

    int updateProductCategoryToNull(long productCategoryId);

    int deleteProduct(@Param("productId") long productId, @Param("shopId") long shopId);
}
