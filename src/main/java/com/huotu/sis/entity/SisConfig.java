/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.entity;

import com.huotu.sis.entity.support.OpenGoodsIdLevelIds;
import com.huotu.sis.entity.support.OpenSisAwards;
import com.huotu.sis.entity.support.SisLevelAwards;
import com.huotu.sis.entity.support.SisRebateTeamManagerSetting;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.Description;

import javax.persistence.*;
import java.util.Date;

/**
 *
 * <p>店中店配置表</p>
 *
 * @author shiliting
 */
@Entity
@Table(name = "SIS_Cfg")
@Getter
@Setter
@Cacheable(value = false)
public class SisConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ConfigId")
    @Description("主键ID")
    private Long id;

    @Column(name = "CustomerId")
    @Description("商户ID")
    private Long merchantId;

    @Column(name = "Enabled")
    @Description("是否启用店中店，0:关闭，1:启用")
    private Integer enabled;

    @Column(name = "Open_Mode")
    @Description("开店门槛，0:小伙伴直接开启，1:购买指定商品")
    private Integer openMode;

    @Column(name = "Open_NeedInvite")
    @Description("是否需要邀请，0:不需要，1:需要")
    private Integer openNeedInvite;

    @Column(name = "RebateSetting")
    @Description("直推奖计算(未使用该字段)")
    private String rebateSetting;


    @Column(name = "Open_GoodsId")
    @Description("购买指定商品")
    private Long openGoodsId;

    @Column(name = "Open_Award",columnDefinition = "longtext")
    @Description("开店奖")
    @Lob
    private OpenSisAwards OpenSisAwards;


    @Column(name = "SisLevel_Open_Awards",columnDefinition = "longtext")
    @Description("等级开店奖个性化")
    @Lob
    private SisLevelAwards sisLevelOpenAwards;

    @Column(name = "SisLevel_Push_Awards",columnDefinition = "longtext")
    @Description("等级直推个性化")
    @Lob
    private SisLevelAwards sisLevelPushAwards;

    @Column(name = "SisLevel_Stock_Awards",columnDefinition = "longtext")
    @Description("等级送股个性化")
    @Lob
    private SisLevelAwards sisLevelStockAwards;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column(name = "RebateTeamManagerSetting",columnDefinition = "longtext")
    @Description("经营者直推奖")
    @Lob
    private SisRebateTeamManagerSetting sisRebateTeamManagerSetting;

    @Column(name = "MaxMartketableNum")
    @Description("最大上架商品数")
    private Integer maxMartketableNum;

    @Column(name = "MaxBrandNum")
    @Description("最大上架品牌数")
    private Integer maxBrandNum;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Description("更新时间")
    @Column(name = "UpdateTime")
    private Date updateTime;


    @Column(name = "CorpStockSelf")
    @Description("开店人获得梦想股")
    private Integer corpStockSelf;


    @Column(name = "CorpStockBelongOne")
    @Description("开店人上级获得梦想股")
    private Integer corpStockBelongOne;

    @Column(name = "ShareTitle",length = 300)
    @Description("分享标题")
    private String shareTitle;

    @Column(name = "ShareDesc",length = 300)
    @Description("分享描述")
    private String ShareDesc;

    @Column(name = "SharePic",length = 200)
    @Description("分享图片")
    private String SharePic;

    @Column(name = "Open_AwardMode")
    @Description("开店返利模式 0:默认八级返利，1：根据店铺的等级返利")
    private Integer openAwardMode;

    @Column(name = "Push_AwardMode")
    @Description("直推返利模式 0:默认返给店主，1：经营者返利模式")
    private Integer pushAwardMode;

    @Column(name = "Open_GoodsMode")
    @Description("开店商品模式 0:一种开店商品，1：每个等级都有一种开店商品")
    private Integer openGoodsMode;

    @Column(name = "Open_GoodsIdlist",columnDefinition = "longtext")
    @Description("个性化商品的和等级")
    @Lob
    private OpenGoodsIdLevelIds openGoodsIdlist;

    @Column(name = "ExtraUpGoodsId")
    @Description("补差价升级商品ID")
    private Long extraUpGoodsId;

    @Description("是否允许店铺升级")
    @Column(name = "EnableLevelUpgrade")
    private Boolean enableLevelUpgrade;

    @Column(name = "HomePageColor",length = 200)
    @Description("分享图片")
    private String homePageColor;

    @Description("是否限制商品和品牌上架个数")
    @Column(name = "Limit_Shelves_Num")
    private Boolean limitShelvesNum;


}
