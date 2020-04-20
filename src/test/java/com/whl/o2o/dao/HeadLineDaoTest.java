package com.whl.o2o.dao;

import com.whl.o2o.entity.HeadLine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
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
public class HeadLineDaoTest {
    @Autowired HeadLineDao headLineDao;

    @Test
    public void testInsert() {
        HeadLine h1 = HeadLine.builder().
                enableStatus(1).
                lineLink("").
                updateTime(new Date()).
                createTime(new Date()).
                lineName("testHL1").
                lineImg("").
                priority(100).build();
        headLineDao.insertHeadLine(h1);
        HeadLine h2 = HeadLine.builder().
                enableStatus(1).
                lineLink("").
                updateTime(new Date()).
                createTime(new Date()).
                lineName("testHL2").
                lineImg("").
                priority(100).build();
        headLineDao.insertHeadLine(h2);
    }

    @Test
    public void testUpdate() {
        HeadLine h = HeadLine.builder().
                lineId(4L).
                enableStatus(1).
                lineLink("").
                updateTime(new Date()).
                lineName("testHeadLine").
                lineImg("").
                priority(100).build();
        headLineDao.updateHeadLine(h);
    }

    @Test
    public void testQueryById() {
        HeadLine headLine = headLineDao.queryHeadLineById(4L);
        System.out.println(headLine.getLineName());
    }

    @Test
    public void testQueryByIds() {
        List<Long> list = new ArrayList<>();
        list.add(3L);
        list.add(4L);
        List<HeadLine> headLineList = headLineDao.queryHeadLineByIds(list);
        System.out.println(headLineList.size());
    }

    @Test
    public void testQueryByCondition() {
        HeadLine h = HeadLine.builder().enableStatus(1).build();
        List<HeadLine> headLineList = headLineDao.queryHeadLine(h);
        System.out.println(headLineList.size());
    }

    @Test
    public void testDeleteById() {
        System.out.println(headLineDao.deleteHeadLine(4));
    }

    @Test
    public void testDeleteByIds() {
        List<Long> list = new ArrayList<>();
        list.add(5L);
        list.add(6L);
        System.out.println(headLineDao.batchDeleteHeadLine(list));
    }
}
