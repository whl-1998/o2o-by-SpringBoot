package com.whl.o2o.service.impl;

import com.whl.o2o.dao.ShopAuthMapDao;
import com.whl.o2o.dao.ShopDao;
import com.whl.o2o.dto.ImageHolder;
import com.whl.o2o.dto.ShopExecution;
import com.whl.o2o.entity.Shop;
import com.whl.o2o.entity.ShopAuthMap;
import com.whl.o2o.enums.ShopStateEnum;
import com.whl.o2o.exceptions.ShopOperationException;
import com.whl.o2o.service.ShopService;
import com.whl.o2o.util.ImageUtil;
import com.whl.o2o.util.PageCalculator;
import com.whl.o2o.util.PathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Service
public class ShopServiceImpl implements ShopService {
    @Autowired
    private ShopDao shopDao;
    @Autowired
    private ShopAuthMapDao shopAuthMapDao;

    private final static Logger logger = LoggerFactory.getLogger(ShopServiceImpl.class);

    @Override
    @Transactional
    public ShopExecution addShop(Shop shop, ImageHolder imageHolder) {
        if (shop == null || shop.getArea() == null || shop.getUserInfo() == null || shop.getShopCategory() == null) {
            return new ShopExecution(ShopStateEnum.EMPTY);
        }
        shop.setEnableStatus(1);//enableStatus初始为0表示店铺状态审核中, 这里为了方便设置为1
        shop.setPriority(0);
        shop.setCreateTime(new Date());
        shop.setUpdateTime(new Date());
        shop.setAdvice("审核通过");
        int effectedNum = shopDao.insertShop(shop);
        if (effectedNum <= 0) {
            logger.error("插入店铺实体失败, 返回0条变更");
            throw new ShopOperationException("店铺创建失败");
        }
        if (imageHolder != null && imageHolder.getImage() != null) {//若传入图片不为空, 则进行相应的店铺缩略图处理
            try {
                addShopImgInputStream(shop, imageHolder);
            } catch (Exception e) {
                logger.error("插入店铺图片失败: " + e.getMessage());
                throw new ShopOperationException("店铺创建失败");
            }
        }
        effectedNum = shopDao.updateShop(shop);
        if (effectedNum <= 0) {
            logger.error("插入店铺实体失败, 返回0条变更");
            throw new ShopOperationException("店铺创建失败");
        }
        ShopAuthMap shopAuthMap = ShopAuthMap.builder().employee(shop.getUserInfo()).shop(shop).title("店家").titleFlag(0).createTime(new Date()).updateTime(new Date()).enableStatus(1).build();
        effectedNum = shopAuthMapDao.insertShopAuthMap(shopAuthMap);
        if (effectedNum <= 0) {
            logger.error("添加店铺授权映射实体类失败, 返回0条变更");
            throw new ShopOperationException("店铺创建失败");
        }
        return new ShopExecution(ShopStateEnum.SUCCESS, shop);
    }

    @Override
    public ShopExecution getByShopId(Long shopId) {
        if (shopId <= 0) {
            return new ShopExecution(ShopStateEnum.EMPTY);
        }
        return new ShopExecution(ShopStateEnum.SUCCESS, shopDao.queryByShopId(shopId));
    }

    @Override
    @Transactional
    public ShopExecution modifyShop(Shop shop, ImageHolder imageHolder) {
        if (shop == null || shop.getShopId() == null) {
            return new ShopExecution(ShopStateEnum.EMPTY);
        }
        //当修改店铺传入新的图片时,需要将旧的图片删除
        if (imageHolder != null && imageHolder.getImage() != null) {
            Shop tempShop = shopDao.queryByShopId(shop.getShopId());
            if (tempShop.getShopImg() != null) {
                ImageUtil.deleteFileOrPath(tempShop.getShopImg());
            }
            try {
                addShopImgInputStream(shop, imageHolder);//在图片系统中添加新的店铺图片, 并将地址设置到shop实体类的相应字段上
            } catch (IOException e) {
                logger.error("插入店铺图片失败: " + e.getMessage());
                throw new ShopOperationException("店铺更新失败");
            }
        }
        shop.setUpdateTime(new Date());
        int effectedNum = shopDao.updateShop(shop);
        if (effectedNum <= 0) {
            logger.error("更新店铺失败, 返回0条变更");
            throw new ShopOperationException("店铺更新失败");
        }
        return new ShopExecution(ShopStateEnum.SUCCESS, shop);
    }

    @Override
    public ShopExecution getShopList(Shop shopCondition, int pageIndex, int pageSize) {
        int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
        List<Shop> shopList = shopDao.queryShopList(shopCondition, rowIndex, pageSize);
        int count = shopDao.queryShopCount(shopCondition);
        ShopExecution shopExecution = new ShopExecution();
        shopExecution.setShopList(shopList);
        shopExecution.setCount(count);
        return shopExecution;
    }

    private void addShopImgInputStream(Shop shop, ImageHolder imageHolder) throws IOException {
        String dest = PathUtil.getShopImagePath(shop.getShopId());
        String shopImgAddr = ImageUtil.generateThumbnail(imageHolder, dest);
        shop.setShopImg(shopImgAddr);
    }
}
