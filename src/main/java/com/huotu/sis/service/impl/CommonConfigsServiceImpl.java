package com.huotu.sis.service.impl;

import com.huotu.sis.service.CommonConfigsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Created by lgh on 2015/12/30.
 */

@Service
public class CommonConfigsServiceImpl implements CommonConfigsService {

    @Autowired
    Environment env;

    @Override
    public String getWebUrl() {
        return env.getProperty("sisweb.weburl", "http://192.168.1.57:8080");
    }

    @Override
    public String getAuthWebUrl() {
        return env.getProperty("auth.web.url", "http://test.auth.huobanplus.com:8081");
    }

    @Override
    public String getAuthKeySecret() {
        return env.getProperty("auth.appsecrect", "1165a8d240b29af3f418b8d10599d0dc");
    }

    public String getAppSecret() {
        return env.getProperty("appsecrect", "9389e8a5c32eefa3134340640fb4ceaa");
    }

    @Override
    public String getAppId() {
        return env.getProperty("appid","73d29a4c9a6d389a0b7288ec27b4c4c4");
    }

    @Override
    public String getMallApiWebUrl() {
        return env.getProperty("com.huotu.huobanplus.mall.api.root","http://mallapi.51flashmall.com");
    }

    @Override
    public String getResourceServerUrl() {
        return env.getProperty("huotu.mall.resourcesUri", "http://res.51flashmall.com");
    }

    @Override
    public String getResourcesHome() {
        return env.getProperty("huotu.resourcesHome", (String) null);
    }

    @Override
    public String getResourcesUri() {
        return env.getProperty("huotu.resourcesUri", (String) null);
    }

    @Override
    public String getNoteSendId() {
        return env.getProperty("huotu.mall.sis.noteId", "0");
    }

    @Override
    public String getMallDomain() {
        return env.getProperty("mall.domain", "51flashmall.com");
    }

    @Override
    public String getResoureServerUrl() {
        return env.getProperty("huotu.mall.resourcesUri", "http://res.51flashmall.com");
    }

}
