package com.huotu.sis.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jinzj on 2016/1/25.
 */
@Getter
@Setter
public class PcSisGoodsModel {

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品图片
     */
    private String imgUrl;

    /**
     * 销售价
     */
    private double price;

    /**
     * 利润
     */
    private double profit;

    /**
     * 分销返利最大值
     */
    private Integer maxRebate;

    /**
     * 分销返利最小值
     */
    private Integer minRebate;

    /**
     * 直推返利
     */
    private Integer directRebate;

    /**
     * 库存量
     */
    private Integer stock;

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
