package com.gt.union.common.util;

import java.util.Date;
import java.util.Random;

/**
 * 随机数工具类
 */
public class RandomKit {
	
	/**
	 * 数字随机
	 */
	public static final int NUMBER = 0;

	/**
	 * 字符随机
	 */
	public static final int CHAR = 1;

	/**
	 * 字符和数字随机
	 */
	public static final int NUMBER_CHAR = 2;
	

	/**
	 * 获取临时文件名 非随机
	 * 
	 * @param filename
	 *            文件名
	 * @return
	 */
	public static String getTempName(String filename) {
		String postfix = "";
		if (filename.indexOf(".") == -1) {
			throw new RuntimeException("文件名错误! params : " + filename);
		}
		postfix = filename.substring(filename.lastIndexOf("."));
		Date nowTime = new Date();
		String name = nowTime.getTime() + postfix;
		return name;
	}

	/**
	 * 生成随机数
	 * @param length 生成长度
	 * @param type 类型 RandomKit.NUMBER RandomKit.CHAR  RandomKit.NUMBER_CHAR
	 * @return
	 */
	public static String getRandomString(int length, int type) {
		String figureBase = "0123456789";
		String letterBase = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String letterAndFigureBase = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		switch (type) {
		case 0:
			for (int i = 0; i < length; i++) {
				int number = random.nextInt(figureBase.length());
				sb.append(figureBase.charAt(number));
			}
			break;
		case 1:
			for (int i = 0; i < length; i++) {
				int number = random.nextInt(letterBase.length());
				sb.append(letterBase.charAt(number));
			}
			break;
		case 2:
			for (int i = 0; i < length; i++) {
				int number = random.nextInt(letterAndFigureBase.length());
				sb.append(letterAndFigureBase.charAt(number));
			}
			break;
		default:
			for (int i = 0; i < length; i++) {
				int number = random.nextInt(figureBase.length());
				sb.append(figureBase.charAt(number));
			}
			break;
		}
		return sb.toString();
	}

	/**
	 * 生成随机数
	 * @param length 生成长度
	 * @param type 类型 RandomKit.NUMBER RandomKit.CHAR  RandomKit.NUMBER_CHAR
	 * @return
	 */
	public static int getRandomInt(int length, int type) {
		return Integer.valueOf(getRandomString(length, type));
	}
}
