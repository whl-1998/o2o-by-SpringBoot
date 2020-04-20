package com.whl.o2o.dao;


import com.whl.o2o.entity.UserProductMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserProductMapDao {
	List<UserProductMap> queryUserProductMapList(
			@Param("userProductCondition") UserProductMap userProductCondition,
			@Param("rowIndex") int rowIndex,
			@Param("pageSize") int pageSize);


	int queryUserProductMapCount(@Param("userProductCondition") UserProductMap userProductCondition);

	int insertUserProductMap(UserProductMap userProductMap);
}
