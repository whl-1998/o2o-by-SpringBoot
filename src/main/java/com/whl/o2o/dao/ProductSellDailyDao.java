package com.whl.o2o.dao;

import java.util.Date;
import java.util.List;

import com.whl.o2o.entity.ProductSellDaily;
import org.apache.ibatis.annotations.Param;

/**
 * @author whl
 * @version V1.0
 * @Title: 商品日销量实体类Dao
 * @Description:
 */
public interface ProductSellDailyDao {
	List<ProductSellDaily> queryProductSellDailyList(
            @Param("productSellDailyCondition") ProductSellDaily productSellDailyCondition,
            @Param("beginTime") Date beginTime,
			@Param("endTime") Date endTime);

	/**
	 * 每天调用一次, 通过quartz定时调度任务
	 * @return
	 */
	int insertProductSellDaily();

	int insertDefaultProductSellDaily();
}
