package com.whl.o2o.dto;

import com.whl.o2o.entity.Award;
import com.whl.o2o.enums.AwardStateEnum;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;

@Data
public class AwardExecution {
	private int state;
	private String stateInfo;
	private int count;
	private Award award;
	private List<Award> awardList;

	@Tolerate
	public AwardExecution() {
	}

	public AwardExecution(AwardStateEnum stateEnum) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}

	public AwardExecution(AwardStateEnum stateEnum, Award award) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.award = award;
	}

	public AwardExecution(AwardStateEnum stateEnum, List<Award> awardList) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.awardList = awardList;
	}
}
