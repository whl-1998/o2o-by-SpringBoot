package com.whl.o2o.dao;

import java.util.Date;
import java.util.List;

import com.whl.o2o.entity.Award;
import com.whl.o2o.entity.Shop;
import com.whl.o2o.entity.UserAwardMap;
import com.whl.o2o.entity.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserAwardMapDaoTest {
	@Autowired
	private UserAwardMapDao userAwardMapDao;

	@Test
	public void testInsert() {
		UserInfo u = UserInfo.builder().userId(1L).build();
		Shop s = Shop.builder().shopId(1L).build();
		UserAwardMap userAwardMap = UserAwardMap.builder().user(u).shop(s).
				createTime(new Date()).point(5).award(Award.builder().awardId(5L).build()).
				usedStatus(1).operator(UserInfo.builder().userId(3L).build()).build();
		userAwardMapDao.insertUserAwardMap(userAwardMap);
	}

	@Test
	public void testUpdate() {
		UserAwardMap userAwardMap = UserAwardMap.builder().
				userAwardId(7L).
				usedStatus(0).
				user(UserInfo.builder().userId(1L).build()).
				operator(UserInfo.builder().userId(3L).build()).
				build();
		userAwardMapDao.updateUserAwardMap(userAwardMap);
	}

	@Test
	public void testQueryById()  {
		UserAwardMap u = userAwardMapDao.queryUserAwardMapById(7L);
		System.out.println(u.getUser().getUsername());
	}

	@Test
	public void testQueryByCondition() {
		UserInfo u = UserInfo.builder().userId(1L).build();
		Shop s = Shop.builder().shopId(1L).build();
		UserAwardMap userAwardMap = UserAwardMap.builder().
				user(u).
				shop(s).
				award(Award.builder().awardId(5l).build()).
				usedStatus(1).
				build();
		List<UserAwardMap> list = userAwardMapDao.queryUserAwardMapList(userAwardMap, 0, 5);
		int count = userAwardMapDao.queryUserAwardMapCount(userAwardMap);
		System.out.println("测试结果:" + list.size() + " " + count);
	}

	@Test
	public void testQueryReceived() {
		UserAwardMap userAwardMap = UserAwardMap.builder().
				award(Award.builder().awardName("测试二").build()).
				usedStatus(1).
				build();
		List<UserAwardMap> list = userAwardMapDao.queryReceivedUserAwardMapList(userAwardMap, 0, 5);
		System.out.println("测试结果:" + list.size());
	}
}
