/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.service;

import com.huotu.sis.exception.InterrelatedException;
import com.huotu.sis.model.NoteModel;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通用验证码服务
 *
 * @author slt
 */
public interface VerificationService {

    /**
     * 发送验证码
     *
     * @param noteModel                 发送验证码的model
     * @throws IllegalStateException    90秒内已发过
     * @throws IllegalArgumentException 手机号码或者其他参数有误
     * @throws NoSuchMethodException    该发送类别不受支持
     * @throws InterrelatedException    第三方平台错误
     */
    @Transactional
    void sendCode(NoteModel noteModel)
            throws IllegalStateException, IllegalArgumentException, NoSuchMethodException, InterrelatedException;

    /**
     * 验证该验证码
     * @param noteModel                 发送验证码的model
     * @return true表示验证通过
     * @throws IllegalArgumentException 手机号码或者其他参数有误
     */
    @Transactional
    boolean verifyCode(NoteModel noteModel) throws IllegalArgumentException;

    /**
     * 可以使用验证码的项目
     */
    enum VerificationProject {
        //【粉猫】您的验证码是{1},为了保护您的账户安全,验证短信请勿转发他人。工作人员不会向你索取验证码。
        fanmore("26807", "(店中店验证码:%s)为了保护您的账户安全，验证短信请勿转发他人。工作人员不会向你索取验证码。"),

        ttds("26808", "开店验证码:%s为了保护您的账户安全，验证短信请勿转发他人。工作人员不会向你索取验证码。");

        private final String format;
        private final String templateId;

        VerificationProject(String templateId, String format) {
            this.templateId = templateId;
            this.format = format;
        }

        public String getTemplateId() {
            return templateId;
        }

        public String getFormat() {
            return format;
        }
    }

}
