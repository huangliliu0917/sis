package com.huotu.sis.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lgh on 2015/12/25.
 */
public class MapHelper {
    public static Map<String, String> getUrlMap(String url) {
        Map<String, String> map = new HashMap<>();
        String params = url.substring(url.indexOf("?") + 1);
        for (String param : params.split("&")) {
            String[] p = param.split("=");
            if (p[0] != null) {
                map.put(p[0], p[1] != null ? p[1] : "");
            }
        }
        return map;
    }
}
