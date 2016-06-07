/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.model.sisweb;

import lombok.Getter;
import lombok.Setter;

/**
 * 店中店商品模型 todo
 * Created by slt on 2015/11/5.
 */
@Getter
@Setter
public class AppSisGoodsModel {

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
     * 返利积分
     */
    private double rebate;

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

//
//    public Long getGoodsId() {
//        return goodsId;
//    }
//
//    public void setGoodsId(Long goodsId) {
//        this.goodsId = goodsId;
//    }
//
//    public String getGoodsName() {
//        return goodsName;
//    }
//
//    public void setGoodsName(String goodsName) {
//        this.goodsName = goodsName;
//    }
//
//    public String getImgUrl() {
//        return imgUrl;
//    }
//
//    public void setImgUrl(String imgUrl) {
//        this.imgUrl = imgUrl;
//    }
//
//
//    public double getPrice() {
//        return price;
//    }
//
//    public void setPrice(double price) {
//        this.price = price;
//    }
//
//    public Integer getStock() {
//        return stock;
//    }
//
//    public void setStock(Integer stock) {
//        this.stock = stock;
//    }
//
//    public double getProfit() {
//        return profit;
//    }
//
//    public void setProfit(double profit) {
//        this.profit = profit;
//    }
//
//
//    public boolean isGoodSelected() {
//        return goodSelected;
//    }
//
//    public void setGoodSelected(boolean goodSelected) {
//        this.goodSelected = goodSelected;
//    }
//
//    public String getDetailsUrl() {
//        return detailsUrl;
//    }
//
//    public void setDetailsUrl(String detailsUrl) {
//        this.detailsUrl = detailsUrl;
//    }
//
//    public double getRebate() {
//        return rebate;
//    }
//
//    public void setRebate(double rebate) {
//        this.rebate = rebate;
//    }
//
//    public String getShareUrl() {
//        return shareUrl;
//    }
//
//    public void setShareUrl(String shareUrl) {
//        this.shareUrl = shareUrl;
//    }
}
