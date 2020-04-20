package com.whl.o2o.dto;

import com.whl.o2o.entity.Area;
import com.whl.o2o.enums.AreaStateEnum;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;

@Data
public class AreaExecution {
	private int state;
	private String stateInfo;
	private int count;
	private Area area;
	private List<Area> areaList;

	@Tolerate
	public AreaExecution() {
	}

	public AreaExecution(AreaStateEnum stateEnum) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
	}

	public AreaExecution(AreaStateEnum stateEnum, Area area) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.area = area;
	}

	public AreaExecution(AreaStateEnum stateEnum, List<Area> areaList) {
		this.state = stateEnum.getState();
		this.stateInfo = stateEnum.getStateInfo();
		this.areaList = areaList;
	}
}
