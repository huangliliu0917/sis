package com.huotu.sis.service.impl;

import com.huotu.sis.service.CommonConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Created by lgh on 2015/11/12.
 */
@Service
public class CommonConfigServiceImpl implements CommonConfigService {

    @Autowired
    Environment environment;

    /**
     * 资源地址
     *
     * @return
     */
    public String getResoureServerUrl() {
        return environment.getProperty("huotu.mall.resourcesUri", "http://res.51flashmall.com");
        //return environment.getProperty("115.28.160.96","115.28.160.96");
    }


    @Override
    public String getMainHost() {
        return environment.getProperty("huotu.sis.web.url", "http://pdmall.51flashmall.com");
    }

    @Override
    public String getResourcesHome() {
        return environment.getProperty("huotu.resourcesHome", (String) null);
    }

    @Override
    public String getResourcesUri() {
        return environment.getProperty("huotu.resourcesUri", (String) null);
    }


}
