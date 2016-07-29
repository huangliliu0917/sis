/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.service.impl;

import com.huotu.sis.entity.VerificationCode;
import com.huotu.sis.exception.InterrelatedException;
import com.huotu.sis.model.sisweb.ResultModel;
import com.huotu.sis.service.VerificationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Formatter;
import java.util.Locale;

/**
 * @author slt
 */
@Service("yimei")
public class YimeiVerificationService extends AbstractVerificationService implements VerificationService {

    private static final Log log = LogFactory.getLog(YimeiVerificationService.class);
    @Autowired
    private Environment env;

    @Autowired
    private SMSHelper sms;

    public YimeiVerificationService() {
        log.debug("伊美短信平台使用中……");
    }

    @Override
    protected void doSend(VerificationCode code) throws InterrelatedException {
//        if (env.acceptsProfiles("test") || env.acceptsProfiles("develop"))
//            return;
        VerificationProject project=VerificationProject.fanmore;
        String smsContent = new Formatter(Locale.CHINA).format(project.getFormat(), code.getCode()).toString();
//        SMSHelperServiceImpl sms = new SMSHelperServiceImpl();
        ResultModel resultSMS = sms.send(code.getMobile(), smsContent);
        if (resultSMS.getCode() != 0)
            throw new InterrelatedException();
    }
}
