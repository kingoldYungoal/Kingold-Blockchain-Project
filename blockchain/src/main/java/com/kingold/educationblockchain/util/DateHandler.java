package com.kingold.educationblockchain.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHandler {
    /*
    获取当前时间
     */
    public String GetCurrentTime(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取String类型的时间
        return sdf.format(date);
    }
}
