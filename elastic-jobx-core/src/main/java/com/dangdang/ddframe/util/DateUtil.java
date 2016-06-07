package com.dangdang.ddframe.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;

/**
 * 日期小工具类
 * @author xiong.j
 */
@Slf4j
public class DateUtil {
	
	public static final String   DEFAULT_FORMAT        = "yyyy-MM-dd HH:mm";
	
	public static final String   MM_DD_YYYY        = "MM/dd/yyyy h:mm a";
	
    /**
     * 解析日期
     * 默认格式: yyyy-MM-dd HH:mm
     * 
     * @param dateString 日期字符串
     * @return java.util.Date
     */
    public static Date parseDate(String dateString) {
        return parseDate(dateString, DEFAULT_FORMAT);
    }
    
    /**
     * 解析日期, 按照默认样式.   
     * 
     * @param dateString 日期字符串
     * @param style 格式
     * @return java.util.Date
     */
    public static Date parseDate(String dateString, String style) {
    	if (isEmpty(dateString) || isEmpty(style)) return null;
    	
        SimpleDateFormat sdf = new SimpleDateFormat(style, Locale.US);
        try {
            return sdf.parse(dateString);
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 比较日期，目标日期是否在开始时间和结束时间之间.   
     * 
     * @param targetDate 目标日期
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return boolean
     */
    public static boolean betweenDate(Date targetDate, Date startDate, Date endDate) {
    	if (startDate == null && endDate == null) return false;
    	if (startDate == null) {
			return targetDate.before(endDate);
		} else if (endDate == null) {
			return startDate.before(targetDate);
		} else {
			return startDate.before(targetDate) && targetDate.before(endDate);
		}
    }
    
    /**
     * 字符串是否为空.   
     * 
     * @param str 字符串
     * @return boolean
     */
    public static boolean isEmpty(String str) {
    	 return str == null || str.length() == 0;
    }
}
