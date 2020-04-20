package com.whl.o2o.dao;

import com.whl.o2o.entity.Area;
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
public class AreaDaoTest {
    @Autowired AreaDao areaDao;

    @Test
    public void testInsert() {
        Area area = Area.builder().areaName("testArea2").createTime(new Date()).priority(100).updateTime(new Date()).build();
        areaDao.insertArea(area);
    }

    @Test
    public void testUpdate() {
        Area area = Area.builder().areaId(5).areaName("testArea2").priority(100).updateTime(new Date()).build();
        areaDao.updateArea(area);
    }

    @Test
    public void testQuery() {
        List<Area> areaList = areaDao.queryArea();
        System.out.println(areaList.size());
    }

    @Test
    public void testDelete() {
        areaDao.deleteArea(5);
    }

    @Test
    public void testBatchDelete() {
        List<Integer> list = new ArrayList<>();
        list.add(6);
        list.add(8);
        areaDao.batchDeleteArea(list);
    }
}
