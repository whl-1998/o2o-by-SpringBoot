package com.whl.o2o.dao;

import com.whl.o2o.entity.Area;

import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface AreaDao {
    List<Area> queryArea();

    int insertArea(Area area);

    int updateArea(Area area);

    int deleteArea(int areaId);

    int batchDeleteArea(List<Integer> areaIdList);
}
