package com.whl.o2o.service.impl;

import com.whl.o2o.dao.ProductDao;
import com.whl.o2o.dao.ProductImgDao;
import com.whl.o2o.dto.ImageHolder;
import com.whl.o2o.dto.ProductExecution;
import com.whl.o2o.entity.Product;
import com.whl.o2o.entity.ProductImg;
import com.whl.o2o.enums.ProductStateEnum;
import com.whl.o2o.exceptions.ProductOperationException;
import com.whl.o2o.service.ProductService;
import com.whl.o2o.util.ImageUtil;
import com.whl.o2o.util.PageCalculator;
import com.whl.o2o.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productDao;

    @Autowired
    private ProductImgDao productImgDao;

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    @Transactional
    public ProductExecution addProduct(Product product, ImageHolder imageHolder, List<ImageHolder> imageHolderList) throws ProductOperationException {
        if (product == null || product.getShop() == null || product.getShop().getShopId() == null) {
            return new ProductExecution(ProductStateEnum.EMPTY);
        }
        product.setCreateTime(new Date());
        product.setUpdateTime(new Date());
        product.setEnableStatus(1);
        product.setPriority(0);
        int effectedNum = productDao.insertProduct(product);
        if (effectedNum <= 0) {
            logger.error("添加商品失败, 返回0条变更");
            throw new ProductOperationException("添加商品失败");
        }
        if (imageHolder != null && imageHolder.getImage() != null) {
            try {
                addImageHolder(product, imageHolder);
            } catch (IOException e) {
                logger.error("添加商品缩略图失败: " + e.getMessage());
                throw new ProductOperationException("添加商品失败");
            }
        }
        if (imageHolderList != null && imageHolderList.size() > 0) {
            try {
                addImageHolderList(product, imageHolderList);
            } catch (Exception e) {
                logger.error("添加商品详情图列表失败: " + e.getMessage());
                throw new ProductOperationException("添加商品失败");
            }
        }
        effectedNum = productDao.updateProduct(product);
        if (effectedNum <= 0) {
            logger.error("添加商品失败, 返回0条变更");
            throw new ProductOperationException("添加商品失败");
        }
        return new ProductExecution(ProductStateEnum.SUCCESS, product);
    }

    @Override
    public ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize) {
        int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
        List<Product> productList = productDao.queryProductList(productCondition, rowIndex, pageSize);
        int count = productDao.queryProductCount(productCondition);
        ProductExecution productExecution = new ProductExecution();
        productExecution.setProductList(productList);
        productExecution.setCount(count);
        return productExecution;
    }

    @Override
    public ProductExecution getProductById(long productId) {
        if (productId <= 0) {
            return new ProductExecution(ProductStateEnum.EMPTY);
        }
        return new ProductExecution(ProductStateEnum.SUCCESS, productDao.queryProductById(productId));
    }

    @Override
    @Transactional
    public ProductExecution modifyProduct(Product product, ImageHolder imageHolder, List<ImageHolder> imageHolderList) {
        if (product == null || product.getShop() == null || product.getShop().getShopId() == null) {
            return new ProductExecution(ProductStateEnum.EMPTY);
        }
        product.setUpdateTime(new Date());
        if (imageHolder != null) {//删除原有缩略图 并添加新的缩略图
            Product oriProduct = productDao.queryProductById(product.getProductId());
            if (oriProduct.getImgAddr() != null) {
                ImageUtil.deleteFileOrPath(oriProduct.getImgAddr());
            }
            try {//添加新的缩略图
                addImageHolder(product, imageHolder);
            } catch (IOException e) {
                logger.error("商品缩略图添加失败: " + e.getMessage());
                throw new ProductOperationException("修改商品失败");
            }
        }
        if (imageHolderList != null && imageHolderList.size() > 0) {//若详情图不为空且原有详情图不为空,删除原有后添加
            deleteImageHolderList(product.getProductId());
            try {
                addImageHolderList(product, imageHolderList);
            } catch (Exception e) {
                logger.error("商品详情图添加失败: " + e.getMessage());
                throw new ProductOperationException("修改商品失败");
            }
        }
        int effectedNum = productDao.updateProduct(product);
        if (effectedNum <= 0) {
            logger.error("更新商品信息失败, 返回0条变更");
            throw new ProductOperationException("修改商品失败");
        }
        return new ProductExecution(ProductStateEnum.SUCCESS, product);
    }

    private void deleteImageHolderList(Long productId) throws ProductOperationException {
        List<ProductImg> productImgList = productImgDao.queryProductImgList(productId);
        if (productImgList.size() <= 0) {
            return;
        }
        for (ProductImg productImg : productImgList) {
            ImageUtil.deleteFileOrPath(productImg.getImgAddr());
        }
        int effectedNum = productImgDao.deleteProductImgByProductId(productId);
        if (effectedNum <= 0) {
            logger.error("删除商品详情图失败失败, 返回0变更");
            throw new ProductOperationException("删除商品详情图失败失败");
        }
    }

    private void addImageHolder(Product product, ImageHolder imageHolder) throws IOException {
        String dest = PathUtil.getShopImagePath(product.getShop().getShopId());
        String thumbnailAddr = ImageUtil.generateThumbnail(imageHolder, dest);
        product.setImgAddr(thumbnailAddr);
    }

    private void addImageHolderList(Product product, List<ImageHolder> imageHolderList) throws Exception {
        String dest = PathUtil.getShopImagePath(product.getShop().getShopId());//获取文件存储的子路径
        List<ProductImg> productImgList = new ArrayList<>();
        for (ImageHolder im : imageHolderList) {
            String imgAddr = ImageUtil.generateThumbnail(im, dest);
            ProductImg productImg = ProductImg.builder().imgAddr(imgAddr).productId(product.getProductId()).priority(0).
                    createTime(new Date()).imgDesc(product.getProductName() + "'s image").build();
            productImgList.add(productImg);
        }
        int effectedNum = productImgDao.batchInsertProductImg(productImgList);
        if (effectedNum <= 0) {
            logger.error("商品详情图添加失败, 返回0条变更");
            throw new ProductOperationException("创建商品详情图失败");
        }
    }
}
