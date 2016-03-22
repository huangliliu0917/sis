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
 * <p>店中店开店奖记录表</p>
 *
 * @author shiliting
 */
@Entity
@Table(name = "SIS_OpenAwardLog")
@Getter
@Setter
@Cacheable(value = false)
public class SisOpenAwardLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LogId")
    @Description("日志ID")
    private Long id;


    @Column(name = "CustomerId")
    @Description("商户ID")
    private Long customerId;


    @Column(name = "ShopId")
    @Description("拿开店奖店主Id(会员ID)")
    private Long shopId;


    @Column(name = "ContribShopId")
    @Description("贡献店主Id(会员ID)")
    private Long contribShopId;


    @Column(name = "Amount")
    @Description("金额")
    private Double amount;

    @Column(name = "Remark")
    @Description("描述")
    private String remark;

    @Column(name = "SrcType")
    @Description("来源(层级)")
    private Integer srcType;


    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "AddTime")
    @Description("添加时间")
    private Date addTime;

    @Column(name = "OrderId")
    @Description("贡献订单号")
    private String orderId;

}
