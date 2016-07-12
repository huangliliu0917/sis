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

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lgh on 2015/11/12.
 */
public class StringHelper {

    private static String appSecret = "1165a8d240b29af3f418b8d10599d0da";

    public static final String SECRET="08afd6f9ae0c6017d105b4ce580de885";

    /**
     * 将json字符串转换为Map，目前只支持“{"56": "颜色","57": "尺码"}”，这种格式的转为Map&lt;Long,String&gt;
     *
     * @param json
     * @return
     */
    public static Map jsonToMap(String json) {
        if (StringUtils.isEmpty(json) || json.equals("{}")) {
            return null;
        }
        String[] spec = json.substring(1, json.length() - 1).replaceAll("\\r|\\n", "").trim().split(",");
        Map<Long, String> map = new HashMap<>();
        for (int i = 0; i < spec.length; i++) {
            String[] ss = spec[i].trim().split(":");
            ss[0] = ss[0].replaceAll("\"", "").trim();
            ss[1] = ss[1].replaceAll("\"", "").trim();
            map.put(Long.parseLong(ss[0]), ss[1]);
        }

        return map;
    }

    /**
     * 验证码生成
     *
     * @param ran  随机设置
     * @param xLen 长度
     * @return
     */
    public static String RandomNum(Random ran, int xLen) {
        String[] char_array = new String[9];
        char_array[0] = "1";
        char_array[1] = "2";
        char_array[2] = "3";
        char_array[3] = "4";
        char_array[4] = "5";
        char_array[5] = "6";
        char_array[6] = "7";
        char_array[7] = "8";
        char_array[8] = "9";

        String output = "";
        double tmp = 0;
        while (output.length() < xLen) {
            tmp = ran.nextFloat();
            output = output + char_array[(int) (tmp * 9)];
        }
        return output;
    }


    /**
     * 通过request 返回一个签名字符串
     *
     * @param request request请求
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getSign(HttpServletRequest request) throws UnsupportedEncodingException {
        Map<String, String> resultMap = new TreeMap<String, String>();
        resultMap.put("appSecret", appSecret);
        Map map = request.getParameterMap();
        for (Object key : map.keySet()) {
            resultMap.put(key.toString(), request.getParameter(key.toString()));
        }

        StringBuilder strB = new StringBuilder();

        resultMap.keySet().stream().filter(key -> !"sign".equals(key)).forEach(key -> strB.append(resultMap.get(key)));

        return DigestUtils.md5DigestAsHex(strB.toString().getBytes("UTF-8")).toLowerCase();
    }

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
        StringBuilder s=new StringBuilder();
        for(int i=1;i<data.length;i++){
            s.append(data[i]);
        }
        s.append(SECRET);
        String sign= DigestUtils.md5DigestAsHex(s.toString().getBytes("UTF-8")).toLowerCase();
        return sign.equals(data[0]);
    }
}