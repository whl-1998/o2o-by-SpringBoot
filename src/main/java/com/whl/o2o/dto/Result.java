package com.whl.o2o.dto;


import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * @author whl
 * @version V1.0
 * @Title:用于封装json对象
 * @Description:
 */
@Data
public class Result<T> {
    private boolean success;
    private T data;
    private String errorMsg;
    private int errorCode;

    @Tolerate
    public Result() {
    }

    public Result(boolean success,T data) {
        this.success = success;
        this.data = data;
    }

    public Result(boolean success, String errorMsg, int errorCode) {
        this.success = success;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
