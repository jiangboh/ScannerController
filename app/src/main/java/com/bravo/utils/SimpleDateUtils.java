package com.bravo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SimpleDateUtils {

	/**
	 * 格式化当前日期
	 * @param format 返回的日期格式 egg:yyyy-MM-dd HH:mm:ss 或HH:mm:ss
	 * @return
	 */
	public static String formatTime(String format){
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
	
	/**
	 * 格式化毫秒数
	 * @param format
	 * @param time 想要转化的时间毫秒值
	 * @return
	 */
	public static String formatTime(String format, long time){
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date(time));
	}
	
	/**
	 * 将日期解析成毫秒数
	 * @param format 日期格式:yyyy-MM-dd HH:mm:ss/yyyy-MM-dd
	 * @param date 要解析的日期
	 * @return
	 */
	public static long parseDate(String format, String date){
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		try {
			Date date1 = formatter.parse(date);
			return date1.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0l;
	}
	
	/**
	 * 将毫秒差转化为：xx小时xx分钟
	 * @param context
	 * @param diffTimes 时间差
	 * @return
	 */
/*	public static String timeToHours(Context context, long diffTimes){
		long tempMinutes = diffTimes/1000;
		long hour = tempMinutes/3600;
		long minutes = tempMinutes%60;
		StringBuffer sb = new StringBuffer();
		sb.append(hour);
		sb.append(context.getString(R.string.hour));
		sb.append(minutes);
		sb.append(context.getString(R.string.minute));
		return sb.toString();
	}*/
	
	/**
	  * 判断是否润年
	  * @param time
	 * @return
	  */
	public static boolean isLeapYear(long time) {
	 
	  /**
	   * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
	   * 3.能被4整除同时能被100整除则不是闰年
	   */
	  Date date = new Date(time);
	  Calendar calendar = Calendar.getInstance();
	  calendar.setTime(date);
	  int year = calendar.get(Calendar.YEAR);
	  if ((year % 400) == 0||((year % 4) == 0&&(year % 100) != 0)){
		  return true;
	  }else{
		  return false;
	  }
	}
	
	/**
	 * 判断是否润年
	 * @param year
	 * @return
	 */
	public static boolean isLeapYear(int year) {
	 
	  /**
	   * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
	   * 3.能被4整除同时能被100整除则不是闰年
	   */
	  if ((year % 400) == 0||((year % 4) == 0&&(year % 100) != 0)){
		  return true;
	  }else{
		  return false;
	  }
	}
	
	/**
	  * 获取一个月的最后一天
	  * @param time 毫秒数
	  * @return 天数
	  */
	public static int getEndDateOfMonth(long time) {
		 Calendar calendar = Calendar.getInstance();
		 calendar.setTime(new Date(time));
		 int month = calendar.get(Calendar.MONTH)+1;
		if(month == 2){
			if(isLeapYear(time)){
				return 29;
			}else{
				return 28;
			}
		}else if(month == 4 || month == 6 || month == 9 || month == 11){
			return 30;
		}else{
			return 31;
		}
	}
	
	/**
	 * 获取一个月的最后一天
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getEndDateOfMonth(int year,int month) {
		if(month == 2){
			if(isLeapYear(year)){
				return 29;
			}else{
				return 28;
			}
		}else if(month == 4 || month == 6 || month == 9 || month == 11){
			return 30;
		}else{
			return 31;
		}
	}
	
	/**
	 * 判断是否是同一天
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDate(Date date1,Date date2){
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		calendar1.setTime(date1);
		calendar2.setTime(date2);
		if(calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
				&&calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
				&&calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断是否是同一天
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isSameDate(Long time1,Long time2){
		if(time1 == null||time2 == null){
			return false;
		}
		return isSameDate(new Date(time1), new Date(time2));
	}
	
	/**
	 * (需要标准时间格式才行)
	 * @param date1 yyyy-MM-dd HH:mm:ss
	 * @param date2 yyyy-MM-dd HH:mm:ss
	 * @param format
	 * @return
	 */
	public static boolean isSameDate(String date1,String date2,String format){
		return isSameDate(parseDate(format, date1), parseDate(format, date2));
	}
}
