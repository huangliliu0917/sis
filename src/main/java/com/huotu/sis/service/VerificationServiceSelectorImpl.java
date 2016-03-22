package com.huotu.sis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by Administrator on 2016/2/27.
 */
@Service
public class VerificationServiceSelectorImpl implements VerificationServiceSelector {
    @Autowired
    @Resource(name = "ttds")
    VerificationService verificationServiceTTDS;

    @Autowired
    @Resource(name = "yimei")
    VerificationService verificationServiceYIMEI;


    @Override
    public VerificationService forMerchant(Long merchantId) {
        if(merchantId==4471L){
            return verificationServiceTTDS;
        }
        return verificationServiceYIMEI;
    }
}
