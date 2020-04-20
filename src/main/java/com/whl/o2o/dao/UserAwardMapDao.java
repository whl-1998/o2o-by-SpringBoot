package com.whl.o2o.dao;

import java.util.List;

import com.whl.o2o.entity.UserAwardMap;
import org.apache.ibatis.annotations.Param;

public interface UserAwardMapDao {
	List<UserAwardMap> queryUserAwardMapList(
			@Param("userAwardCondition") UserAwardMap userAwardCondition,
			@Param("rowIndex") int rowIndex,
			@Param("pageSize") int pageSize);


	List<UserAwardMap> queryReceivedUserAwardMapList(
			@Param("userAwardCondition") UserAwardMap userAwardCondition,
			@Param("rowIndex") int rowIndex,
			@Param("pageSize") int pageSize);

	int queryUserAwardMapCount(@Param("userAwardCondition") UserAwardMap userAwardCondition);

	UserAwardMap queryUserAwardMapById(long userAwardId);

	int insertUserAwardMap(UserAwardMap userAwardMap);

	int updateUserAwardMap(UserAwardMap userAwardMap);
}
