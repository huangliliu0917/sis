package com.huotu.sis.model.sisweb;

import lombok.Getter;
import lombok.Setter;

/**
 * 用于存储店主精选的商品的model
 */
@Getter
@Setter
public class SisRecommendGoodsModel {
    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品图片
     */
    private String img;

    /**
     * 库存
     */
    private Long stock;

    /**
     * 最大分销积分
     */
    private Integer maxDistributionIntegral;

    /**
     * 最小分销积分
     */
    private Integer minDistributionIntegral;

    /**
     * 会员价
     */
    private double memberPrice;

    /**
     * 直推积分
     */
    private Integer shopRebate;

    /**
     * 是否上架
     */
    private boolean goodSelected;

    /**
     * 商品详情URL
     */
    private String detailsUrl;

    /**
     * 商品分享URL
     */
    private String shareUrl;
}
