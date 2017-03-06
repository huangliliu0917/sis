package com.huotu.sis.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by lgh on 2015/11/12.
 */
public class DateHelper {

    private Log log = LogFactory.getLog(DateHelper.class);

    /**
     * 计算积分转正时间
     * @param positiveDay   转正所需天数
     * @return
     */
    public static Date countPositiveTime(int positiveDay){
        Date date=new Date();
        Calendar ca=Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.DATE,positiveDay+1);
        Date now=ca.getTime();//转正后的时间
        Date now2 = DateUtil.makeStartDate(now);
        return now2;
    }

}
