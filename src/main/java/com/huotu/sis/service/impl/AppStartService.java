/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.service.impl;

import com.huotu.sis.common.StringHelper;
import com.huotu.sis.entity.SisOpenAwardLog;
import com.huotu.sis.repository.SisOpenAwardLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 项目启动完成时执行的一些操作
 * Created by slt on 2016/5/16.
 */

@Service
public class AppStartService implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    SisOpenAwardLogRepository sisOpenAwardLogRepository;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            List<SisOpenAwardLog> sisOpenAwardLogs=sisOpenAwardLogRepository.findByPosMonthTagIsNull();
            if(sisOpenAwardLogs!=null&&!sisOpenAwardLogs.isEmpty()){
                for(SisOpenAwardLog s:sisOpenAwardLogs){
                    s.setPosMonthTag(StringHelper.getPosMonthTag(s.getAddTime()));
                }
                sisOpenAwardLogRepository.save(sisOpenAwardLogs);
            }
        }
    }

}
