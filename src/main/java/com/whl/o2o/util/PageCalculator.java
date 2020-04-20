package com.whl.o2o.util;

/**
 * @author whl
 * @version V1.0
 * @Title: 分页工具类
 * @Description:
 */
public class PageCalculator {
    public static int calculateRowIndex(int pageIndex,int pageSize){
        return pageIndex > 0 ? (pageIndex - 1) * pageSize : 0;
    }
}
