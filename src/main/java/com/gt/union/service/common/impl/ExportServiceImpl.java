package com.gt.union.service.common.impl;

import com.gt.union.service.common.ExportService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/10 0010.
 */
@Service
public class ExportServiceImpl implements ExportService {

	@Override
	public HSSFWorkbook exportBusMemberCar(String[] titles, String[] contentName, List<Map<String, Object>> list) {
		return null;
	}

	@Override
	public HSSFWorkbook exportUnionMember(String[] titles, String[] contentName, List<Map<String, Object>> list) {
		return null;
	}

	@Override
	public HSSFWorkbook exportCardDivide(String[] titles, String[] contentName, List<Map<String, Object>> list) {
		return null;
	}

	@Override
	public HSSFWorkbook exportBrokerageDetail(String[] titles, String[] contentName, List<Map<String, Object>> list) {
		return null;
	}

	@Override
	public HSSFWorkbook exportRecommendBrokerageDetail(String[] titles, String[] contentName, List<Map<String, Object>> list) {
		return null;
	}

	@Override
	public HSSFWorkbook exportConsumeFromDetail(String[] titles, String[] contentName, List<Map<String, Object>> list) {
		return null;
	}

	@Override
	public HSSFWorkbook exportConsumeToDetail(String[] titles, String[] contentName, List<Map<String, Object>> list) {
		return null;
	}
}
