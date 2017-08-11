package com.gt.union.common.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 导出excel工具类
 * Created by Administrator on 2017/8/10 0010.
 */
public class ExportUtil {

	/**
	 * 输出导出excel文件
	 * @param response
	 * @param wb
	 * @param filename
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static void responseExport(HttpServletResponse response, HSSFWorkbook wb, String filename){
		OutputStream os = null;
		try {
			response.reset();
			// 先去掉文件名称中的空格,然后转换编码格式为utf-8,保证不出现乱码,这个文件名称用于浏览器的下载框中自动显示的文件名
			os = new BufferedOutputStream(
					response.getOutputStream());
			response.setHeader("Content-Disposition", "attachment;filename=\"" +
					URLEncoder.encode(filename+
							DateTimeKit.format(new Date())+ ".xls", "UTF-8") + "\"");
			response.setContentType("application/vnd.ms-excel");
			wb.write(os);// 输出文件
			os.flush();
			os.close();
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			try {
				if(os != null) os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
