package com.huotu.sis.service.impl;


import com.huotu.sis.service.CommonConfigsService;
import com.huotu.sis.service.SecurityService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by lgh on 2015/12/21.
 */
@Service
public class SecurityServiceImpl implements SecurityService {

    private static Log log = LogFactory.getLog(SecurityServiceImpl.class);
    @Autowired
    private CommonConfigsService commonConfigService;

    @Override
    public String getSign(HttpServletRequest request) throws UnsupportedEncodingException {
        Map<String, String> resultMap = new TreeMap<String, String>();

        Map map = request.getParameterMap();
        for (Object key : map.keySet()) {
            resultMap.put(key.toString(), request.getParameter(key.toString()));
        }

        return getMapSign(resultMap);
    }

//    @Override
//    public String getSign(String url) throws UnsupportedEncodingException {
//        Map<String, String> resultMap = MapHelper.getUrlMap(url);
//        return getMapSign(resultMap);
//    }



    @Override
    public String getMapSign(Map<String, String> map) throws UnsupportedEncodingException {
        Map<String, String> resultMap = new TreeMap<String, String>();
        for (Object key : map.keySet()) {
            resultMap.put(key.toString(), map.get(key));
        }

        StringBuilder strB = new StringBuilder();
        for (String key : resultMap.keySet()) {
            if (!"sign".equals(key) && !StringUtils.isEmpty(resultMap.get(key))) {
                strB.append("&" + key + "=" + resultMap.get(key));
            }
        }

        String toSign = (strB.toString().length() > 0 ? strB.toString().substring(1) : "") + commonConfigService.getAuthKeySecret();
        log.info(toSign);
        return DigestUtils.md5DigestAsHex(toSign.getBytes("UTF-8")).toLowerCase();
    }

    public String getMapSignByAppSecret(Map<String, String> map) throws UnsupportedEncodingException {
        Map<String, String> resultMap = new TreeMap<String, String>();
        for (Object key : map.keySet()) {
            resultMap.put(key.toString(), map.get(key));
        }

        StringBuilder strB = new StringBuilder();
        for (String key : resultMap.keySet()) {
            if (!"sign".equals(key) && !StringUtils.isEmpty(resultMap.get(key))) {
                strB.append("&" + key + "=" + resultMap.get(key));
            }
        }

        String toSign = (strB.toString().length() > 0 ? strB.toString().substring(1) : "") + commonConfigService.getAppSecret();
        log.info(toSign);
        return DigestUtils.md5DigestAsHex(toSign.getBytes("UTF-8")).toLowerCase();
    }
}
