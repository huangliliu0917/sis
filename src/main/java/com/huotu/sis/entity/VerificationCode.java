/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.entity;


import com.huotu.sis.model.sisweb.VerificationType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * 验证码
 */
@Entity
@Getter
@Setter
@Cacheable(value = false)
@Table(indexes = {@Index(columnList = "mobile"), @Index(columnList = "sendTime")})
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 发送验证码的场景
     */
    private VerificationType type;

    /**
     * 验证码
     */
    private String code;

    /**
     * 发送时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date sendTime;

    /**
     * 商户ID
     */
    private Long customerId;
}