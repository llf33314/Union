package com.gt.union.service.common.impl;

import com.gt.union.common.util.CommonUtil;
import com.gt.union.common.util.DateTimeKit;
import com.gt.union.common.util.StringUtil;
import com.gt.union.service.common.ExportService;
import org.apache.poi.hssf.usermodel.*;
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
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleCenter = wb.createCellStyle();
		HSSFSheet sheet = createHSSFSheet(titles, wb, styleCenter);
		for (int i=0;i<list.size();i++) {
			Map<String, Object> item = list.get(i);
			HSSFRow row = sheet.createRow(i + 1);
			for(int j=0;j<titles.length;j++){
				String key = contentName[j];
				String c = CommonUtil.isEmpty(item.get(key)) ? "" : item.get(key).toString();
				if("card_type".equals(key)){//卡类型
					if("1".equals(c)){
						c = "黑卡";
					}else if("2".equals(c)){
						c = "红卡";
					}
				}else if("cardTermTime".equals(key)){//卡有效期
					if(StringUtil.isEmpty(c)){
						c = "无";
					}else {
						c = DateTimeKit.format(DateTimeKit.parse(c,DateTimeKit.DEFAULT_DATETIME_FORMAT), "yyyy/MM/dd");
					}
				}else if("updatetime".equals(key)){//升级时间
					c = DateTimeKit.format(DateTimeKit.parse(c,DateTimeKit.DEFAULT_DATETIME_FORMAT), "yyyy/MM/dd HH:mm");
				}else if("integral".equals(key)){//积分
					if(StringUtil.isEmpty(c)){
						c = "0";
					}
				}
				HSSFCell cell = row.createCell(j);
				cell.setCellValue(c);
				cell.setCellStyle(styleCenter);
			}

		}
		return wb;
	}

	@Override
	public HSSFWorkbook exportUnionMember(String[] titles, String[] contentName, List<Map<String, Object>> list) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle styleCenter = wb.createCellStyle();
		HSSFSheet sheet = createHSSFSheet(titles, wb, styleCenter);
		for (int i=0;i<list.size();i++) {
			Map<String, Object> item = list.get(i);
			HSSFRow row = sheet.createRow(i + 1);
			for(int j=0;j<titles.length;j++){
				String key = contentName[j];
				String c = CommonUtil.isEmpty(item.get(key)) ? "" : item.get(key).toString();
				if("fromDiscount".equals(key)){//我给他折扣
					if(StringUtil.isEmpty(c)){
						c = "无";
					}
				}else if("toDiscount".equals(key)){//他给我折扣
					if(StringUtil.isEmpty(c)){
						c = "无";
					}
				}else if("createtime".equals(key)){//加入时间
					c = DateTimeKit.format(DateTimeKit.parse(c,DateTimeKit.DEFAULT_DATETIME_FORMAT), "yyyy-MM-dd HH:mm");
				}else if("sellDivideProportion".equals(key)){//售卡分成比例
					if(StringUtil.isEmpty(c)){
						c = "无";
					}else {
						c +="%";
					}
				}
				HSSFCell cell = row.createCell(j);
				cell.setCellValue(c);
				cell.setCellStyle(styleCenter);
			}

		}
		return wb;
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


	/**
	 * 创建sheet
	 * @param titles
	 * @param wb
	 * @param styleCenter
	 * @return
	 */
	private HSSFSheet createHSSFSheet(String[] titles, HSSFWorkbook wb, HSSFCellStyle styleCenter){
		HSSFSheet sheet = wb.createSheet("sheet1");
		HSSFRow rowTitle = sheet.createRow(0);
		HSSFCellStyle styleTitle = wb.createCellStyle();
		styleTitle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont fontTitle = wb.createFont();
		fontTitle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		fontTitle.setFontName("宋体");
		fontTitle.setFontHeight((short) 200);
		styleTitle.setFont(fontTitle);

		HSSFCell cellTitle = null;
		for(int i=0;i<titles.length;i++){
			cellTitle = rowTitle.createCell(i);
			cellTitle.setCellValue(titles[i]);
			cellTitle.setCellStyle(styleTitle);
		}
		styleCenter.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		return sheet;
	}
}
