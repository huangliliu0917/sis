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
import com.huotu.sis.repository.VerificationCodeRepository;
import com.huotu.sis.common.SysRegex;
import com.huotu.sis.exception.InterrelatedException;
import com.huotu.sis.model.NoteModel;
import com.huotu.sis.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author slt
 */
public abstract class AbstractVerificationService implements VerificationService {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    /**
     * 允许间隔600秒
     */
    private int gapSeconds = 60;

    /**
     * 验证码有效时间
     */
    private int validSeconds=600;

    @Transactional
    public void sendCode(NoteModel noteModel)
            throws IllegalStateException, IllegalArgumentException, NoSuchMethodException, InterrelatedException {
        if (!SysRegex.IsValidMobileNo(noteModel.getPhone())) {
            throw new IllegalArgumentException("号码不对");
        }
        VerificationCode verificationCode = verificationCodeRepository.findByMobileAndTypeAndCustomerId(
                noteModel.getPhone(), noteModel.getType(),noteModel.getMerchantId());
        if (verificationCode != null) {
            //刚刚发送过
            if (noteModel.getDate().getTime() - verificationCode.getSendTime().getTime() < gapSeconds * 1000) {
                throw new IllegalStateException("刚刚发过");
            }
        } else {
            verificationCode = new VerificationCode();
            verificationCode.setMobile(noteModel.getPhone());
            verificationCode.setCustomerId(noteModel.getMerchantId());
            verificationCode.setType(noteModel.getType());
        }
        verificationCode.setSendTime(noteModel.getDate());
        verificationCode.setCode(noteModel.getCode());
        verificationCode = verificationCodeRepository.save(verificationCode);

        doSend(verificationCode);
    }

    protected abstract void doSend(VerificationCode code) throws InterrelatedException;

    @Transactional
    public boolean verifyCode(NoteModel noteModel) throws IllegalArgumentException {
        if (!SysRegex.IsValidMobileNo(noteModel.getPhone())) {
            throw new IllegalArgumentException("号码不对");
        }
        List<VerificationCode> codeList =
                verificationCodeRepository.findByMobileAndCustomerIdAndTypeAndSendTimeGreaterThan(
                noteModel.getPhone(),
                        noteModel.getMerchantId(),
                        noteModel.getType(),
                        new Date(noteModel.getDate().getTime() - validSeconds * 1000));
        for (VerificationCode verificationCode : codeList) {
            if (verificationCode.getCode().equals(noteModel.getCode()))
                return true;
        }
        return false;
    }

    public int getValidSeconds() {
        return validSeconds;
    }

    public void setValidSeconds(int validSeconds) {
        this.validSeconds = validSeconds;
    }

    public int getGapSeconds() {
        return gapSeconds;
    }

    public void setGapSeconds(int gapSeconds) {
        this.gapSeconds = gapSeconds;
    }
}
