/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.entity;

import com.huotu.huobanplus.common.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.Description;

import javax.persistence.*;
import java.util.Date;

/**
 * 店中店申请实体。
 *
 * <p>小伙伴向商家申请开店时产生的一条记录</p>
 *
 * @author shiliting
 */
@Entity
@Table(name = "SIS_SisApply")
@Getter
@Setter
@Cacheable(value = false)
public class SisApply {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Description("主键ID")
    private Long id;
    /**
     * 申请人
     */
    @ManyToOne
    @Description("申请人")
    private User user;

    @Description("真实姓名")
    private String name;
    /**
     * 申请时间
     */
    @Temporal(value = TemporalType.TIMESTAMP)
    @Description("申请时间")
    private Date applyTime;

    /**
     * 申请人电话
     */
    @Description("申请人电话")
    private String mobile;


    /**
     * 申请状态 0.申请中 1.申请成功 2.被拒绝  todo 暂定这么多
     */
    @Description("申请状态")
    private Integer applyStatus;

    /**
     * 申请被拒绝的原因
     */
    @Description("申请被拒绝的原因")
    private String refuseReason;
}
