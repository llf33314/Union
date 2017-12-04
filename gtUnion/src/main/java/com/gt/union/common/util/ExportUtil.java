package com.gt.union.common.util;

import org.apache.poi.hssf.usermodel.*;
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
     *
     * @param response
     * @param wb
     * @param filename
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static void responseExport(HttpServletResponse response, HSSFWorkbook wb, String filename) throws Exception {
        response.reset();
        // 先去掉文件名称中的空格,然后转换编码格式为utf-8,保证不出现乱码,这个文件名称用于浏览器的下载框中自动显示的文件名
        OutputStream os = new BufferedOutputStream(response.getOutputStream());
        response.setHeader("Content-Disposition",
                "attachment;filename=\"" + URLEncoder.encode(filename + DateTimeKit.format(new Date()) + ".xls",
                        "UTF-8") + "\"");
        response.setContentType("application/vnd.ms-excel");
        // 输出文件
        wb.write(os);
        os.flush();
    }

    /**
     * 返回导出失败信息
     *
     * @param response
     * @throws IOException
     */
    public static void responseExportError(HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        String result = "<script>alert('导出失败')</script>";
        PrintWriter writer = response.getWriter();
        writer.print(result);
        writer.close();
    }

    /**
     * 根据标题头创建workbook对象
     *
     * @param titles 标题头
     * @return HSSFWorkbook
     */
    public static HSSFWorkbook newHSSFWorkbook(String[] titles) {
        HSSFWorkbook wb = new HSSFWorkbook();
        //默认样式
        HSSFCellStyle cellStyle = newHSSFCellStyle(wb, HSSFCellStyle.ALIGN_CENTER);
        HSSFFont font = newHSSFFont(wb, HSSFFont.BOLDWEIGHT_BOLD, "宋体", (short) 200);
        cellStyle.setFont(font);

        //default
        HSSFSheet sheet = wb.createSheet("sheet1");
        HSSFRow rowTitle = sheet.createRow(0);
        for (int i = 0, length = titles.length; i < length; i++) {
            HSSFCell cellTitle = rowTitle.createCell(i);
            cellTitle.setCellValue(titles[i]);
            cellTitle.setCellStyle(cellStyle);
        }
        return wb;
    }

    /**
     * 根据workbook对象和对齐属性，获取单元格样式对象
     *
     * @param wb
     * @param align
     * @return
     */
    public static HSSFCellStyle newHSSFCellStyle(HSSFWorkbook wb, short align) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(align);
        return cellStyle;
    }

    /**
     * 根据workbook对象、粗细属性、字体名称和字体大小，获取字体格式对象
     *
     * @param wb
     * @param boldWeight
     * @param fontName
     * @param fontHeight
     * @return
     */
    public static HSSFFont newHSSFFont(HSSFWorkbook wb, short boldWeight, String fontName, short fontHeight) {
        HSSFFont font = wb.createFont();
        font.setBoldweight(boldWeight);
        font.setFontName(fontName);
        font.setFontHeight(fontHeight);
        return font;
    }
}
