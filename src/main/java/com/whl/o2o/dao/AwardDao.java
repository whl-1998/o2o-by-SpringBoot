package com.whl.o2o.dao;

import java.util.List;

import com.whl.o2o.entity.Award;
import org.apache.ibatis.annotations.Param;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface AwardDao {
	List<Award> queryAwardList(@Param("awardCondition") Award awardCondition, @Param("rowIndex") int rowIndex, @Param("pageSize") int pageSize);

	int queryAwardCount(@Param("awardCondition") Award awardCondition);

	Award queryAwardByAwardId(long awardId);

	int insertAward(Award award);

	int updateAward(Award award);

	int deleteAward(@Param("awardId") long awardId, @Param("shopId") long shopId);
}
