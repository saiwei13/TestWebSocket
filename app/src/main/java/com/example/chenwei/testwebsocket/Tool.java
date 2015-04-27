package com.example.chenwei.testwebsocket;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chenwei on 4/23/15.
 */
public class Tool {

    private static String format = "yyyyMMddHHmmss";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);


    /**
     * 将标准格式的时间转化成系统时间
     * ex: 20140610142702------>1402381622815
     * @param time
     * @return
     */
    public static long getSystemFormatTime(String time){
        long tmp_long_time = 0 ;
        try {
            tmp_long_time = simpleDateFormat.parse(time).getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tmp_long_time;
    }

    private static String format_2 = "yyyy/MM/dd";
    private static SimpleDateFormat simpleDateFormat_2 = new SimpleDateFormat(format_2);

    /**
     * ex： 2014/06/18
     * @param _time
     * @return
     */
    public static String getYearMonthDay(long _time){
        return simpleDateFormat_2.format(new Date(_time));
    }

    /**
     * 获取当前时间
     * @return
     */
    public static String getCurTime(){
        return getYearMonthDay(System.currentTimeMillis());
    }
}
