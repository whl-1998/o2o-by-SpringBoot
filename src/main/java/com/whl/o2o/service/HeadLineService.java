package com.whl.o2o.service;

import com.whl.o2o.dto.HeadLineExecution;
import com.whl.o2o.dto.ImageHolder;
import com.whl.o2o.entity.HeadLine;

import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public interface HeadLineService {
    String HEAD_LINE_LIST= "headlinelist";

    HeadLineExecution getHeadLineList(HeadLine headLineCondition);

    HeadLineExecution addHeadLine(HeadLine headLine, ImageHolder thumbnail);

    HeadLineExecution modifyHeadLine(HeadLine headLine, ImageHolder thumbnail);

    HeadLineExecution removeHeadLine(long headLineId);

    HeadLineExecution removeHeadLineList(List<Long> headLineIdList);
}
