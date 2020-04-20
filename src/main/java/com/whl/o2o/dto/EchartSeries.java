package com.whl.o2o.dto;

import lombok.Data;
import java.util.List;

/**
 * 迎合echart里的series项
 */
@Data
public class EchartSeries {
	private String name;
	private String type = "bar";
	private List<Integer> data;
}
