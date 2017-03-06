package com.huotu.sis.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jinzj on 2016/2/15.
 */
public class DateUtil {

    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static String formatDate(Date date, String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static Date parse(String date, String format) {
        DateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date makeEndDate(Date date){
        String dateString = formatDate(date, DATE_FORMAT) + " 23:59:59";
        return parse(dateString, DATETIME_FORMAT);
    }

    public static Date makeStartDate(Date date){
        String dateString = formatDate(date, DATE_FORMAT) + " 00:00:00";
        return parse(dateString, DATETIME_FORMAT);
    }

}
