package com.whl.o2o.service.impl;

import com.whl.o2o.dao.ProductCategoryDao;
import com.whl.o2o.dao.ProductDao;
import com.whl.o2o.dto.ProductCategoryExecution;
import com.whl.o2o.dto.ProductExecution;
import com.whl.o2o.entity.Product;
import com.whl.o2o.entity.ProductCategory;
import com.whl.o2o.enums.ProductCategoryStateEnum;
import com.whl.o2o.enums.ProductStateEnum;
import com.whl.o2o.exceptions.ProductCategoryOperationException;
import com.whl.o2o.service.ProductCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {
    @Autowired
    private ProductCategoryDao productCategoryDao;
    @Autowired
    private ProductDao productDao;

    private final static Logger logger = LoggerFactory.getLogger(ProductCategoryServiceImpl.class);

    @Override
    public ProductCategoryExecution getProductCategoryList(Long shopId) {
        if (shopId <= 0) {
            return new ProductCategoryExecution(ProductCategoryStateEnum.EMPTY);
        }
        return new ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS, productCategoryDao.queryProductCategoryList(shopId));
    }

    @Override
    public ProductCategoryExecution batchAddProductCategory(List<ProductCategory> productCategoryList) {
        if (productCategoryList == null || productCategoryList.size() <= 0) {
            return new ProductCategoryExecution(ProductCategoryStateEnum.EMPTY);
        }
        for (ProductCategory pc : productCategoryList) {
            pc.setCreateTime(new Date());
        }
        int effectedNum = productCategoryDao.batchInsertProductCategory(productCategoryList);
        if (effectedNum <= 0 || effectedNum != productCategoryList.size()) {
            logger.error("添加商品分类失败, 返回错误的变更次数");
            throw new ProductCategoryOperationException("商品类别创建失败");
        }
        return new ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS);
    }

    @Override
    @Transactional
    public ProductCategoryExecution deleteProductCategory(Long productCategoryId, Long shopId) {
        if (productCategoryId <= 0 || shopId <= 0) {
            return new ProductCategoryExecution(ProductCategoryStateEnum.EMPTY);
        }
        int effectedNum = productDao.updateProductCategoryToNull(productCategoryId);//删除前,将删除的商品类别下的商品的类别id置为null
        if (effectedNum <= 0 ) {
            logger.error("商品类别下的所有商品的product_category_id字段未被成功置为null");
            throw new ProductCategoryOperationException("商品类别删除失败");
        }
        effectedNum = productCategoryDao.deleteProductCategory(productCategoryId,shopId);
        if (effectedNum <= 0) {
            logger.error("商品类别删除失败, 返回0变更");
            throw new ProductCategoryOperationException("商品类别删除失败");
        }
        return new ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS);
    }
}
