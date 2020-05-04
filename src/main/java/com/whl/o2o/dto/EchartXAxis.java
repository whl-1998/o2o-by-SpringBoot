package com.whl.o2o.dto;

import lombok.Data;

import java.util.HashSet;

/**
 * 迎合echart里的x轴
 */
@Data
public class EchartXAxis {
	private static final String type = "category";
	private HashSet<String> data;
}
