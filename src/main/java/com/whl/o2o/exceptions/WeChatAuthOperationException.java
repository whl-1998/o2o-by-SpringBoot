package com.whl.o2o.exceptions;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
public class WeChatAuthOperationException extends RuntimeException{
    public WeChatAuthOperationException(String msg){
        super(msg);
    }
}
