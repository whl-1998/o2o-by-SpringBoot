package com.whl.o2o.dto;

import com.whl.o2o.entity.WeChatAuth;
import com.whl.o2o.enums.WeChatAuthStateEnum;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;

/**
 * @author whl
 * @version V1.0
 * @Title:
 * @Description:
 */
@Data
public class WeChatAuthExecution {
    private int state;//结果状态
    private String stateInfo; //状态标识,用于解释结果状态
    private int count;
    private WeChatAuth weChatAuth;
    private List<WeChatAuth> weChatAuthList;

    @Tolerate
    public WeChatAuthExecution() {
    }

    public WeChatAuthExecution(WeChatAuthStateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    public WeChatAuthExecution(WeChatAuthStateEnum stateEnum, WeChatAuth weChatAuth) {
        this.stateInfo = stateEnum.getStateInfo();
        this.state = stateEnum.getState();
        this.weChatAuth = weChatAuth;
    }

    public WeChatAuthExecution(WeChatAuthStateEnum stateEnum, List<WeChatAuth> weChatAuthList) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.weChatAuthList = weChatAuthList;
    }
}
