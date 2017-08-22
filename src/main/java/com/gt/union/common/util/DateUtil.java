package com.gt.union.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017/8/22 0022.
 */
public class DateUtil {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取当前时间
     * @return
     */
    public static final Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }

    /**
     * 获取字符形式的当前时间
     * @return
     */
    public static final String getCurrentDateString() {
        return getDateString(getCurrentDate(), DATETIME_PATTERN);
    }

    /**
     * 获取指定格式、字符形式的当期时间
     * @param pattern
     * @return
     */
    public static final String getCurrentDateString(String pattern) {
        return getDateString(getCurrentDate(), pattern);
    }

    /**
     * 获取指定格式、字符形式的当期时间
     * @param date
     * @param pattern
     * @return
     */
    public static final String getDateString(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    /**
     * 将字符形式时间解析为日期形式
     * @param date
     * @return
     * @throws ParseException
     */
    public static final Date parseDate(String date) throws ParseException{
        return parseDate(date, DATETIME_PATTERN);
    }

    /**
     * 将字符形式时间按指定格式解析为日期形式
     * @param date
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static final Date parseDate(String date, String pattern) throws ParseException{
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.parse(date);
    }

    /**
     * 获取当前时间的星期几形式
     * @return
     */
    public static final String getCurrentWeek(){
        return getWeek(getCurrentDate());
    }

    /**
     * 获取指定时间的星期几形式
     * @param date
     * @return
     */
    public static final String getWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
            default:
                return "";
        }
    }

    public static final Date addDays(Date date, Integer amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, amount);
        return calendar.getTime();
    }
}
