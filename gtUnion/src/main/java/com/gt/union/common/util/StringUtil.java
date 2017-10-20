package com.gt.union.common.util;

/**
 * Created by Administrator on 2017/7/26 0026.
 */
public class StringUtil {
	/**
	 * 判断字符串是否为空，如为空，则返回true，否则返回false
	 *
	 * @param str
	 * @return
	 */
	public static final boolean isEmpty(String str) {
		return str == null || "".equals(str.trim());
	}

	/**
	 * 判断字符串是否不为空，如不为空，则返回true，否则返回false
	 *
	 * @param str
	 * @return
	 */
	public static final boolean isNotEmpty(String str) {
		return str != null && !"".equals(str.trim());
	}


	/**
	 * 判断字符串中英文的长度
	 * @param str
	 * @return
	 */
	public static double getStringLength(String str) {
		if(CommonUtil.isEmpty(str)){
			return 0;
		}
		double valueLength = 0;
		String chinese = "[\u4e00-\u9fa5]";       // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
		for (int i = 0; i < str.length(); i++) {         // 获取一个字符
			String temp = str.substring(i, i + 1);         // 判断是否为中文字符
			if (temp.matches(chinese)) {           // 中文字符长度为1
				valueLength += 1;
			} else {           // 其他字符长度为0.5
				valueLength += 0.5;
			}
		}       //进位取整
		return Math.ceil(valueLength);
	}

	public static void main(String[] args) {
		String str = "阿斯利いaa";
		System.out.println(getStringLength(str));
	}

	/**
	 * 格式化字符串
	 *
	 * @param format
	 * @param args
	 * @return
	 */
	public static String format(String format, Object... args) {
		String str = null;
		str = String.format(format, args);
		return str;
	}
}
