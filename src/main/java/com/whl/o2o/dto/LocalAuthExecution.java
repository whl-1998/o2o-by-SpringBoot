package com.whl.o2o.dto;

import com.whl.o2o.entity.LocalAuth;
import com.whl.o2o.enums.LocalAuthStateEnum;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;

@Data
public class LocalAuthExecution {
    private int state;
    private String stateInfo;
    private int count;
    private LocalAuth localAuth;
    private List<LocalAuth> localAuthList;

    @Tolerate
    public LocalAuthExecution() {
    }

    public LocalAuthExecution(LocalAuthStateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
    }

    public LocalAuthExecution(LocalAuthStateEnum stateEnum, LocalAuth localAuth) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.localAuth = localAuth;
    }

    public LocalAuthExecution(LocalAuthStateEnum stateEnum, List<LocalAuth> localAuthList) {
        this.state = stateEnum.getState();
        this.stateInfo = stateEnum.getStateInfo();
        this.localAuthList = localAuthList;
    }
}
