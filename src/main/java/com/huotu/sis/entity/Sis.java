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
 * 店铺。
 *
 * <p>小伙伴的店中店</p>
 *
 * @author shiliting
 */
@Entity
@Getter
@Setter
@Cacheable(value = false)
public class Sis {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Description("主键ID")
    private Long id;
    /**
     * 店主(小伙伴)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @Description("店主(小伙伴)")
    private User user;

    /**
     * 店铺等级
     */
    @OneToOne(fetch = FetchType.LAZY)
    @Description("店铺等级")
    private SisLevel sisLevel;

    /**
     * 店中店状态
     */
    @Description("店中店状态")
    private boolean status;

    /**
     * 店中店标题
     */
    @Description("店中店标题")
    private String title;

    /**
     * 店中店图片
     */
    @Description("店中店图片")
    private String imgPath;

    /**
     *店中店描述
     */
    @Description("店铺描述")
    private String description;

    /**
     * 店中店模板 todo
     */
    //@ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REFRESH})
    @Description("店中店模板")
    private Long templateId;

    /**
     * 分享标题
     */
    @Description("分享标题")
    private String shareTitle;


    /**
     * 分享描述
     */
    @Description("分享描述")
    private String shareDesc;

    /**
     * 开店时间
     */
    @Temporal(value = TemporalType.TIMESTAMP)
    @Description("开店时间")
    private Date openTime;

    @Description("店主的真实姓名")
    @Column(name = "RealName",length = 20)
    private String realName;

    @Description("店主的手机号")
    @Column(name = "Mobile",length = 20)
    private String mobile;

    @Description("商户ID")
    @Column(name = "CustomerId")
    private Long customerId;

    @Description("是否已经一键铺货")
    @Column(name = "ShelvesAllGoods")
    private Boolean shelvesAllGoods;

}
