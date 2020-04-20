package com.whl.o2o.service.impl;

import java.util.Date;
import java.util.List;

import com.whl.o2o.dao.ShopAuthMapDao;
import com.whl.o2o.dto.ShopAuthMapExecution;
import com.whl.o2o.entity.ShopAuthMap;
import com.whl.o2o.enums.ShopAuthMapStateEnum;
import com.whl.o2o.exceptions.ShopAuthMapOperationException;
import com.whl.o2o.service.ShopAuthMapService;
import com.whl.o2o.util.PageCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Email;

@Service
public class ShopAuthMapServiceImpl implements ShopAuthMapService {
	@Autowired
	private ShopAuthMapDao shopAuthMapDao;

    private static final Logger logger = LoggerFactory.getLogger(ShopAuthMapServiceImpl.class);

	@Override
	public ShopAuthMapExecution listShopAuthMapByShopId(long shopId, int pageIndex, int pageSize) {
		if (shopId <= 0 || pageIndex <= 0 && pageSize <= 0) {
            return new ShopAuthMapExecution(ShopAuthMapStateEnum.EMPTY);
		}
        int beginIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
        List<ShopAuthMap> shopAuthMapList = shopAuthMapDao.queryShopAuthMapListByShopId(shopId, beginIndex, pageSize);
        int count = shopAuthMapDao.queryShopAuthCountByShopId(shopId);
        ShopAuthMapExecution se = new ShopAuthMapExecution();
        if (count == shopAuthMapList.size()) {
			se.setShopAuthMapList(shopAuthMapList);
			se.setCount(count);
		} else {
			return new ShopAuthMapExecution(ShopAuthMapStateEnum.INNER_ERROR);
		}
        return se;
	}

	@Override
	public ShopAuthMapExecution getShopAuthMapById(long shopAuthId) {
		if (shopAuthId <= 0) {
			return new ShopAuthMapExecution(ShopAuthMapStateEnum.EMPTY);
		}
		return new ShopAuthMapExecution(ShopAuthMapStateEnum.SUCCESS, shopAuthMapDao.queryShopAuthMapById(shopAuthId));
	}

	@Override
	@Transactional
	public ShopAuthMapExecution addShopAuthMap(ShopAuthMap shopAuthMap) {
		if (shopAuthMap == null || shopAuthMap.getShop() == null || shopAuthMap.getShop().getShopId() == null
				|| shopAuthMap.getEmployee() == null || shopAuthMap.getEmployee().getUserId() == null) {
            return new ShopAuthMapExecution(ShopAuthMapStateEnum.EMPTY);
        }
        shopAuthMap.setCreateTime(new Date());
        shopAuthMap.setUpdateTime(new Date());
        shopAuthMap.setEnableStatus(1);
        int effectedNum = shopAuthMapDao.insertShopAuthMap(shopAuthMap);
        if (effectedNum <= 0) {
            logger.error("添加店铺授权失败, 返回0条变更");
            throw new ShopAuthMapOperationException("添加授权失败");
        }
        return new ShopAuthMapExecution(ShopAuthMapStateEnum.SUCCESS, shopAuthMap);
    }

	@Override
	@Transactional
	public ShopAuthMapExecution modifyShopAuthMap(ShopAuthMap shopAuthMap) {
		if (shopAuthMap == null || shopAuthMap.getShopAuthId() == null) {
            return new ShopAuthMapExecution(ShopAuthMapStateEnum.EMPTY);
        }
        shopAuthMap.setUpdateTime(new Date());
        int effectedNum = shopAuthMapDao.updateShopAuthMap(shopAuthMap);
        if (effectedNum <= 0) {
            logger.error("修改店铺授权失败");
            throw new ShopAuthMapOperationException("添加授权失败");
        }
        return new ShopAuthMapExecution(ShopAuthMapStateEnum.SUCCESS, shopAuthMap);
	}
}
