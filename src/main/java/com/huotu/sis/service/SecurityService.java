package com.huotu.sis.service;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by lgh on 2015/12/21.
 */
public interface SecurityService {

    /**
     * 获取加密后的sign
     *
     * @param request
     * @return
     * @throws UnsupportedEncodingException
     */
    String getSign(HttpServletRequest request) throws UnsupportedEncodingException;

    /**
     * 获取加密后的sign
     *
     * @param url url地址
     * @return
     * @throws UnsupportedEncodingException
     */
//    String getSign(String url) throws UnsupportedEncodingException;

    /**
     * 获得加密后的sign
     * @param resultMap
     * @return
     * @throws UnsupportedEncodingException
     */
    String getMapSign(Map<String, String> resultMap) throws UnsupportedEncodingException;


    /**
     * 用于用于mallapi
     * @param map
     * @return
     * @throws UnsupportedEncodingException
     */
    String getMapSignByAppSecret(Map<String, String> map) throws UnsupportedEncodingException;
}
