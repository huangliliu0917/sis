package com.huotu.sis.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 用于发送验证码的model
 */
@Getter
@Setter
public class NoteModel {
    /**
     * 电话
     */
    String phone;

    /**
     * 验证码
     */
    String code;

    /**
     * 发送的时间
     */
    Date date;

    /**
     * 场景
     */
    VerificationType type;

    /**
     * 商家ID
     */
    Long merchantId;
}
