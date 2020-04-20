package com.whl.o2o.service.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whl.o2o.cache.JedisUtil;
import com.whl.o2o.dao.ShopCategoryDao;
import com.whl.o2o.dto.ImageHolder;
import com.whl.o2o.dto.ShopCategoryExecution;
import com.whl.o2o.entity.ShopCategory;
import com.whl.o2o.enums.ShopCategoryStateEnum;
import com.whl.o2o.exceptions.AreaOperationException;
import com.whl.o2o.exceptions.ShopCategoryOperationException;
import com.whl.o2o.service.CacheService;
import com.whl.o2o.service.ShopCategoryService;
import com.whl.o2o.util.ImageUtil;
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
public class ShopCategoryServiceImpl implements ShopCategoryService {
    @Autowired
    private ShopCategoryDao shopCategoryDao;
    @Autowired
    private JedisUtil.Keys jedisKeys;
    @Autowired
    private JedisUtil.Strings jedisStrings;
    @Autowired
    private CacheService cacheService;

    private static final Logger logger = LoggerFactory.getLogger(ShopCategoryServiceImpl.class);

    @Override
    public ShopCategoryExecution getShopCategoryList(ShopCategory shopCategoryCondition) {
        String key = SHOP_CATEGORY_LIST;
        List<ShopCategory> shopCategoryList;
        ObjectMapper mapper = new ObjectMapper();
        if (shopCategoryCondition == null) {// 若查询条件为空, 检索所有ShopCategory, 将key设置为shopcategorylist_allfirstlevel
            key = key + "_allfirstlevel";
        } else if (shopCategoryCondition.getParent() != null && shopCategoryCondition.getParent().getShopCategoryId() != null) {
            // 若parentId为非空, 检索指定父分类下的子分类, 将key设置为shopcategorylist_parent + 父类别的Id
            key = key + "_parent" + shopCategoryCondition.getParent().getShopCategoryId();
        } else {// 列出所有子类别, 将key设置为shopcategorylist_allsecondlevel
            key = key + "_allsecondlevel";
        }
        if (!jedisKeys.exists(key)) {
            shopCategoryList = shopCategoryDao.queryShopCategory(shopCategoryCondition);
            String jsonString;
            try {
                jsonString = mapper.writeValueAsString(shopCategoryList);
            } catch (JsonProcessingException e) {
                logger.error("对象转换jsonString格式失败: " + e.getMessage());
                throw new ShopCategoryOperationException("获取店铺分类失败");
            }
            jedisStrings.set(key, jsonString);
        } else {
            String jsonString = jedisStrings.get(key);
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, ShopCategory.class);
            try {
                shopCategoryList = mapper.readValue(jsonString, javaType);
            } catch (IOException e) {
                logger.error("jsonString转换为javaType失败: " + e.getMessage());
                throw new AreaOperationException(e.getMessage());
            }
        }
        return new ShopCategoryExecution(ShopCategoryStateEnum.SUCCESS, shopCategoryList);
    }

    @Override
    @Transactional
    public ShopCategoryExecution addShopCategory(ShopCategory shopCategory, ImageHolder thumbnail) {
        if (shopCategory == null) {
            return new ShopCategoryExecution(ShopCategoryStateEnum.EMPTY);
        }
        shopCategory.setCreateTime(new Date());
        shopCategory.setUpdateTime(new Date());
        shopCategory.setPriority(0);
        if (thumbnail != null) {
            try {
                addThumbnail(shopCategory, thumbnail);
            } catch (IOException e) {
                logger.error("添加店铺分类图片失败: " + e.getMessage());
                throw new ShopCategoryOperationException("添加店铺分类失败");
            }
        }
        int effectedNum = shopCategoryDao.insertShopCategory(shopCategory);
        if (effectedNum <= 0) {
            logger.error("添加店铺分类失败, 返回0条变更");
            throw new ShopCategoryOperationException("添加店铺分类失败");
        }
        cacheService.removeFromCache(SHOP_CATEGORY_LIST);
        return new ShopCategoryExecution(ShopCategoryStateEnum.SUCCESS, shopCategory);
    }

    @Override
    public ShopCategoryExecution getShopCategoryById(Long shopCategoryId) {
        if (shopCategoryId <= 0) {
            return new ShopCategoryExecution(ShopCategoryStateEnum.EMPTY);
        }
        return new ShopCategoryExecution(ShopCategoryStateEnum.SUCCESS, shopCategoryDao.queryShopCategoryById(shopCategoryId));
    }

    @Override
    @Transactional
    public ShopCategoryExecution modifyShopCategory(ShopCategory shopCategory, ImageHolder thumbnail) {
        if (shopCategory == null || shopCategory.getShopCategoryId() == null || shopCategory.getShopCategoryId() <= 0) {
            return new ShopCategoryExecution(ShopCategoryStateEnum.EMPTY);
        }
        shopCategory.setUpdateTime(new Date());
        if (thumbnail != null) {
            ShopCategory tempShopCategory = shopCategoryDao.queryShopCategoryById(shopCategory.getShopCategoryId());
            if (tempShopCategory != null && tempShopCategory.getShopCategoryImg() != null) {
                ImageUtil.deleteFileOrPath(tempShopCategory.getShopCategoryImg());
            }
            try {// 存储新的图片
                addThumbnail(shopCategory, thumbnail);
            } catch (IOException e) {
                logger.error("添加店铺分类失败, 返回0条变更: " + e.getMessage());
                throw new ShopCategoryOperationException("添加店铺分类失败");
            }
        }
        int effectedNum = shopCategoryDao.updateShopCategory(shopCategory);
        if (effectedNum <= 0) {
            logger.error("更新店铺类别信息失败, 返回0条变更");
            throw new ShopCategoryOperationException("添加店铺分类失败");
        }
        cacheService.removeFromCache(SHOP_CATEGORY_LIST);
        return new ShopCategoryExecution(ShopCategoryStateEnum.SUCCESS, shopCategory);
    }

    private void addThumbnail(ShopCategory shopCategory, ImageHolder thumbnail) throws IOException {
        String dest = PathUtil.getShopCategoryPath();
        String thumbnailAddr = ImageUtil.generateNormalImg(thumbnail, dest);
        shopCategory.setShopCategoryImg(thumbnailAddr);
    }
}
