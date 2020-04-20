package com.whl.o2o.dao;

import com.whl.o2o.entity.Award;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AwardDaoTest {
    @Autowired
    private AwardDao awardDao;

    @Test
    public void testInsert() {
        Award award = Award.builder().awardName("测试1").awardDesc("test").awardImg("test").point(5).priority(1).enableStatus(1).
                createTime(new Date()).updateTime(new Date()).shopId(1L).build();
        awardDao.insertAward(award);
    }

    @Test
    public void testQueryById() {
        System.out.println(awardDao.queryAwardByAwardId(7).getAwardName());
    }

    @Test
    public void testQueryByCondition() {
        Award a = Award.builder().awardName("测试").enableStatus(1).shopId(1L).build();
        List<Award> awards = awardDao.queryAwardList(a, 0, 5);
        int count = awardDao.queryAwardCount(a);
        System.out.println(awards.size() + " " + count);
    }

    @Test
    public void testUpdate() {
        awardDao.updateAward(Award.builder().awardDesc("test").awardId(7L).shopId(1L).
                updateTime(new Date()).shopId(1L).build());
    }

    @Test
    public void testDel() {
        Award awardCondition = Award.builder().awardName("测试").build();
        awardDao.deleteAward(7L, 1L);
    }
}
