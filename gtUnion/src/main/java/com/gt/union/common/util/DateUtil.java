package com.gt.union.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author linweicong
 * @version 2017-11-22 17:45:00
 */
public class DateUtil {
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String SERIAL_PATTERN = "yyyyMMddHHmmssSSS";

    /**
     * 获取当前时间
     *
     * @return Date
     */
    public static Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }

    /**
     * 获取字符形式的当前时间
     *
     * @return String
     */
    public static String getCurrentDateString() {
        return getDateString(getCurrentDate(), DATETIME_PATTERN);
    }

    /**
     * 获取指定格式、字符形式的当期时间
     *
     * @param pattern String
     * @return String
     */
    public static String getCurrentDateString(String pattern) {
        return getDateString(getCurrentDate(), pattern);
    }

    /**
     * 获取指定格式、字符形式的当期时间
     *
     * @param date    Date
     * @param pattern String
     * @return String
     */
    public static String getDateString(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    /**
     * 将字符形式时间解析为日期形式
     *
     * @param date String
     * @return Date
     * @throws ParseException 转换异常
     */
    public static Date parseDate(String date) throws ParseException {
        return parseDate(date, DATETIME_PATTERN);
    }

    /**
     * 将字符形式时间按指定格式解析为日期形式
     *
     * @param date    String
     * @param pattern String
     * @return Date
     * @throws ParseException 转换异常
     */
    public static Date parseDate(String date, String pattern) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.parse(date);
    }

    /**
     * 获取指定时间的星期几形式，中文
     *
     * @param date Date
     * @return String
     */
    public static String getChDayInWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                return "星期天";
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

    /**
     * 获取指定时间的星期几形式，英文
     *
     * @param date Date
     * @return String
     */
    public static String getEnDayInWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return "";
        }
    }

    /**
     * 加天数
     *
     * @param date   日期
     * @param amount 天数
     * @return Date
     */
    public static Date addDays(Date date, Integer amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, amount);
        return calendar.getTime();
    }

    /**
     * 加月数
     *
     * @param date   日期
     * @param amount 月数
     * @return Date
     */
    public static Date addMonths(Date date, Integer amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, amount);
        return calendar.getTime();
    }

    /**
     * 加年数
     *
     * @param date   日期
     * @param amount 年数
     * @return Date
     */
    public static Date addYears(Date date, Integer amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, amount);
        return calendar.getTime();
    }

    /**
     * 获取流水号格式的当前时间
     *
     * @return String
     */
    public static String getSerialNumber() {
        return getCurrentDateString(SERIAL_PATTERN);
    }

    /**
     * 获取退盟过渡期中剩余天数
     *
     * @param fromDate 当前时间
     * @param toDate   确认退盟时间
     * @return 相隔天数
     */
    public static Integer getPeriodIntervalDay(Date fromDate, Date toDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(fromDate);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(toDate);

        Integer intervalDay = toCalendar.get(Calendar.DAY_OF_YEAR) - fromCalendar.get(Calendar.DAY_OF_YEAR);

        Integer toHour = toCalendar.get(Calendar.HOUR);
        Integer fromHour = fromCalendar.get(Calendar.HOUR);
        if (toHour > fromHour) {
            intervalDay++;
        } else if (toHour < fromHour) {
            intervalDay--;
        } else {
            Integer toMinute = toCalendar.get(Calendar.MINUTE);
            Integer fromMinute = fromCalendar.get(Calendar.MINUTE);
            if (toMinute > fromMinute) {
                intervalDay++;
            } else if (toMinute < fromMinute) {
                intervalDay--;
            } else {
                Integer toSecond = toCalendar.get(Calendar.SECOND);
                Integer fromSecond = fromCalendar.get(Calendar.SECOND);
                if (toSecond > fromHour) {
                    intervalDay++;
                } else if (toSecond < fromSecond) {
                    intervalDay--;
                }
            }
        }

        return intervalDay < 0 ? 0 : intervalDay;
    }

    /**
     * 获取本周星期一的日期
     *
     * @return Date
     */
    public static Date getMondayInWeek() {
        int offset = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2 + 7) % 7;
        return DateUtil.addDays(DateUtil.getCurrentDate(), 0 - offset);
    }

}
