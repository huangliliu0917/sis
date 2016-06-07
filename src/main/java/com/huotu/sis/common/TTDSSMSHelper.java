/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.common;


import com.alibaba.fastjson.JSON;
import com.huotu.sis.model.sisweb.ResultModel;
import org.springframework.util.DigestUtils;

import java.util.Base64;
import java.util.Date;

public class TTDSSMSHelper {
    private static String serverUrl = "http://api.ttk.cn:22220/InterfacePlatform/ApiService";
    private static String appKey = "TTDS";
    private static String pswd = "gi0Q1GX6zd";
    private static String serviceCode="SendSms";

    public TTDSSMSHelper() {
    }

    public ResultModel send(String mobile, String msg) {
        ResultModel resultModel = new ResultModel();
        resultModel.setCode(-1000);
        resultModel.setMessage("未知错误");

        try {
            String errMsg = "";
            int code = 10000;
            String param = "{\"list\":[{\"phone\": \"" + mobile + "\", \"content\":\"" + msg + "\" }]}";
            Long date=new Date().getTime();
            String digest= Base64.getEncoder().encodeToString(
                    DigestUtils.md5DigestAsHex(
                            (appKey + serviceCode + param + date + pswd).getBytes("utf-8")
                    ).getBytes("utf-8")
            );
            String text = HttpSender.ttdsSend(serverUrl, appKey,param,serviceCode,digest,date);
            resultModel=JSON.parseObject(text,ResultModel.class);
        } catch (Exception ex) {
        }
        return resultModel;
    }
}
