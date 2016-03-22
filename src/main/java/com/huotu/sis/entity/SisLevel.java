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

/**
 *
 * <p>店中店等级表</p>
 *
 * @author shiliting
 */
@Entity
@Table(name = "SIS_Level")
@Getter
@Setter
@Cacheable(value = false)
public class SisLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LevelId")
    @Description("主键ID")
    private Long id;

    @Column(name = "CustomerId")
    @Description("商户ID")
    private Long merchantId;

    @Column(name = "LevelName",length = 80)
    @Description("等级名称")
    private String levelName;

    @Column(name = "LevelNo")
    @Description("等级序号")
    private Integer levelNo;

    @Column(name = "Remark",length = 1000)
    @Description("备注")
    private String Rrmark;

    @Column(name = "Up_ShopNum")
    @Description("等级升级子店数")
    private Integer upShopNum;

    @Column(name = "Up_TeamShopNum")
    @Description("等级升级子店数")
    private Integer upTeamShopNum;

    @Column(name = "IsSystem")
    @Description("1:系统等级，不允许删除，0:后期添加的等级，可以删除")
    private Integer isSystem;

    @Column(name = "RebateRate")
    @Description("直推奖")
    private double rebateRate;


}
