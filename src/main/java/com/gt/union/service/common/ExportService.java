package com.gt.union.service.common;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;
import java.util.Map;

/**
 * 导出excel 服务类
 * Created by Administrator on 2017/8/10 0010.
 */
public interface ExportService {

	/**
	 * 导出盟员联盟卡列表
	 * @param titles	标题数组
	 * @param contentName	内容标题数组
	 * @param list		导出列表
	 * @return
	 */
	HSSFWorkbook exportBusMemberCar(String[] titles, String[] contentName, List<Map<String, Object>> list);

	/**
	 * 导出盟员列表
	 * @param titles	标题数组
	 * @param contentName	内容标题数组
	 * @param list		导出列表
	 * @return
	 */
	HSSFWorkbook exportUnionMember(String[] titles, String[] contentName, List<Map<String, Object>> list);

	/**
	 * 导出售卡分成
	 * @param titles	标题数组
	 * @param contentName	内容标题数组
	 * @param list		导出列表
	 * @return
	 */
	HSSFWorkbook exportCardDivide(String[] titles, String[] contentName, List<Map<String, Object>> list);

	/**
	 * 导出佣金明细详情
	 * @param titles	标题数组
	 * @param contentName	内容标题数组
	 * @param list		导出列表
	 * @return
	 */
	HSSFWorkbook exportBrokerageDetail(String[] titles, String[] contentName, List<Map<String, Object>> list);

	/**
	 * 导出商机推荐明细
	 * @param titles	标题数组
	 * @param contentName	内容标题数组
	 * @param list		导出列表
	 * @return
	 */
	HSSFWorkbook exportRecommendBrokerageDetail(String[] titles, String[] contentName, List<Map<String, Object>> list);

	/**
	 * 导出本店消费记录列表
	 * @param titles	标题数组
	 * @param contentName	内容标题数组
	 * @param list		导出列表
	 * @return
	 */
	HSSFWorkbook exportConsumeFromDetail(String[] titles, String[] contentName, List<Map<String, Object>> list);

	/**
	 * 导出他店消费记录列表
	 * @param titles	标题数组
	 * @param contentName	内容标题数组
	 * @param list		导出列表
	 * @return
	 */
	HSSFWorkbook exportConsumeToDetail(String[] titles, String[] contentName, List<Map<String, Object>> list);
}
