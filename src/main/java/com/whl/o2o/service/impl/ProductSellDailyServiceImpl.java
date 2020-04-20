package com.whl.o2o.service.impl;

import java.util.Date;
import java.util.List;

import com.whl.o2o.dao.ProductSellDailyDao;
import com.whl.o2o.entity.ProductSellDaily;
import com.whl.o2o.service.ProductSellDailyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductSellDailyServiceImpl implements ProductSellDailyService {
	@Autowired
	private ProductSellDailyDao productSellDailyDao;

	private static final Logger log = LoggerFactory.getLogger(ProductSellDailyServiceImpl.class);

	@Override
	public List<ProductSellDaily> listProductSellDaily(ProductSellDaily productSellDailyCondition, Date beginTime, Date endTime) {
		return productSellDailyDao.queryProductSellDailyList(productSellDailyCondition, beginTime, endTime);
	}

	@Override
	public void dailyCalculate() {
		log.info("Quartz 跑起来了!");
		productSellDailyDao.insertProductSellDaily();// 统计在tb_user_product_map里面产生销量的每个店铺的各件商品的日销量
		productSellDailyDao.insertDefaultProductSellDaily();// 统计余下的商品的日销量，全部置为0（为了迎合echarts的数据请求）
	}
}
