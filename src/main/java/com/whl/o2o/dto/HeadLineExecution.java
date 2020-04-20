package com.whl.o2o.dto;

import com.whl.o2o.entity.HeadLine;
import com.whl.o2o.enums.HeadLineStateEnum;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;

@Data
public class HeadLineExecution {
	private int state;
	private String stateInfo;
	private int count;
	private HeadLine headLine;
	private List<HeadLine> headLineList;

	@Tolerate
	public HeadLineExecution() {
	}

	public HeadLineExecution(HeadLineStateEnum stateEnum) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}

	public HeadLineExecution(HeadLineStateEnum stateEnum, HeadLine headLine) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.headLine = headLine;
	}

	public HeadLineExecution(HeadLineStateEnum stateEnum, List<HeadLine> headLineList) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.headLineList = headLineList;
	}
}
