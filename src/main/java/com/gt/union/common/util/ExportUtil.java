package com.gt.union.common.util;

import com.gt.union.common.exception.DataExportException;
import com.gt.union.common.response.GTJsonResult;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;

/**
 * 导出excel工具类
 * Created by Administrator on 2017/8/10 0010.
 */
public class ExportUtil {

	private static Logger logger = LoggerFactory.getLogger(ExportUtil.class);

	private static final String RESPONSE_EXPORT = "ExportUtil.responseExport()";

	/**
	 * 输出导出excel文件
	 * @param response
	 * @param wb
	 * @param filename
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static void responseExport(HttpServletResponse response, HSSFWorkbook wb, String filename) throws Exception {
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
		}catch (Exception e){
			logger.error("",e);
			response.reset();
			response.setContentType("text/html");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			String result = GTJsonResult.instanceErrorMsg(RESPONSE_EXPORT,e.getMessage(),"导出失败").toString();
			PrintWriter writer = response.getWriter();
			writer.print(result);
			writer.close();
			throw new DataExportException("导出异常");
		}finally {
			try {
				if(os != null) os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
