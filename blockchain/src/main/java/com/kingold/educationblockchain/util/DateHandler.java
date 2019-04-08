package com.kingold.educationblockchain.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHandler {
	public static String DATE_FORMAT_CHINESE = "yyyy年M月d日";
    /*
    获取当前时间
     */
    public String GetCurrentTime(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取String类型的时间
        return sdf.format(date);
    }
    
    
    public static String formatChinese(String dateStr) throws ParseException{
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Date date = sdf.parse(dateStr);
    	sdf = new SimpleDateFormat(DATE_FORMAT_CHINESE);
    	return sdf.format(date);
    }
}
