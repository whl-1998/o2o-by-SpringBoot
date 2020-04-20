package com.whl.o2o.entity;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Date;

/**
 * @author whl
 * @version V1.0
 * @Title:头条实体类
 * @Description:
 */
@Data
@Builder
public class HeadLine {
    private Long lineId;
    private String lineName;
    private String lineLink;
    private String lineImg;
    private Integer priority;
    private Integer enableStatus;//0：不可用 1：可用
    private Date createTime;
    private Date updateTime;

    @Tolerate
    public HeadLine() {
    }
}
