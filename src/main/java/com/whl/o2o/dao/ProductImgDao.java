package com.whl.o2o.dao;

import com.whl.o2o.entity.ProductImg;

import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface ProductImgDao {
    List<ProductImg> queryProductImgList(Long productId);

    int batchInsertProductImg(List<ProductImg> productImgList);

    int deleteProductImgByProductId(Long productId);
}
