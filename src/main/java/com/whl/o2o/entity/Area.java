package com.whl.o2o.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Date;

/**
 * @author whl
 * @version V1.0
 * @Title:区域实体类对象
 * @Description:
 */
@Data
@Builder
public class Area {
    private Integer areaId;//ID
    private String areaName;//Name
    private Integer priority;//权重
    private Date createTime;//创建时间
    private Date updateTime;//更新时间

    @Tolerate
    public Area() {
    }
}
