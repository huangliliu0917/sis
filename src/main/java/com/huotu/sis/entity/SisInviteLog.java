/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.Description;

import javax.persistence.*;
import java.util.Date;

/**
 *
 * <p>店中店邀请开店记录表</p>
 *
 * @author shiliting
 */
@Entity
@Table(name = "SIS_InviteLog")
@Getter
@Setter
@Cacheable(value = false)
public class SisInviteLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LogId")
    @Description("日志ID")
    private Long id;

    @Column(name = "Mobile",length = 20)
    @Description("手机号")
    private String mobile;


    @Column(name = "WeChatId",length = 20)
    @Description("微信号")
    private String weChatId;


    @Column(name = "InviterId")
    @Description("邀请人会员Id")
    private Long inviterId;


    @Column(name = "AcceptId")
    @Description("接受邀请会员Id")
    private Long acceptId;

    @Column(name ="RealName")
    @Description("真实姓名")
    private String realName;

    @Column(name = "AcceptTime")
    @Description("添加时间")
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date acceptTime;

    @Description("商户ID")
    @Column(name = "CustomerId")
    private Long customerId;


}
