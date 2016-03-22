/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.common;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

public class StringHelper {
    public StringHelper() {
    }

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if(!StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(",");
            return index != -1?ip.substring(0, index):ip;
        } else {
            ip = request.getHeader("X-Real-IP");
            return !StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)?ip:request.getRemoteAddr();
        }
    }

    public static String randomNo(Random ran, int xLen) {
        String[] char_array = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "W", "U", "V", "X", "Y", "Z"};
        String output = "";

        for(double tmp = 0.0D; output.length() < xLen; output = output + char_array[(int)(tmp * 34.0D)]) {
            tmp = (double)ran.nextFloat();
        }

        return output;
    }

    public static String randomNum(Random ran, int xLen) {
        String[] char_array = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String output = "";

        for(double tmp = 0.0D; output.length() < xLen; output = output + char_array[(int)(tmp * 9.0D)]) {
            tmp = (double)ran.nextFloat();
        }

        return output;
    }
}
