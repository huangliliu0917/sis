/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.common;

import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符处理
 *
 * @author Administrator
 */
public class StringHelper {
    public static final String SECRET="08afd6f9ae0c6017d105b4ce580de885";


    /**
     * 返回app信息
     * @param userAgent 字符串
     * @return
     */
    public static String[] getRequestAppInfo(String userAgent){
        if(StringUtils.isEmpty(userAgent)){
            return null;
        }
        Pattern p = Pattern.compile(";hottec:([^;]+)");
        Matcher matcher=p.matcher(userAgent);
        StringBuilder builder=new StringBuilder();
        while (matcher.find()){
            builder.append(matcher.group(1));
        }
        return builder.toString().split(":");

    }

    /**
     * 判断签名是否正确
     * @param data
     * @return
     */
    public static boolean isTrueSign(String[] data) throws UnsupportedEncodingException {
        for(String s:data){
            if(StringUtils.isEmpty(s)){
                return false;
            }
        }
        String s=data[1]+data[2]+SECRET;
        String sign= DigestUtils.md5DigestAsHex(s.getBytes("UTF-8")).toLowerCase();
        return sign.equals(data[0]);
    }
}
