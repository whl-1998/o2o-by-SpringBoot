package com.whl.o2o.service;

import com.whl.o2o.dto.AreaExecution;
import com.whl.o2o.entity.Area;

import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface AreaService {
    String AREA_LIST_KEY = "arealist";

    AreaExecution getAreaList();

    AreaExecution addArea(Area area);

    AreaExecution modifyArea(Area area);
}
