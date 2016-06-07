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
 * 店中店基本数据模型
 * 用于给app返回店中店的基本信息，包括店铺名称，头像，模板信息等
 * Created by slt on 2015/11/3.
 */
@Getter
@Setter
public class AppSisBaseInfoModel {

    /**
     * 店铺Id
     */
    private Long sisId;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 店中店标题
     */
    private String title;

    /**
     * 店铺描述
     */
    private String sisDescription;

    /**
     * 店中店头像
     */
    private String imgUrl;

    /**
     * 是否开启店中店
     */
    private boolean enableSis;

    /**
     * 首页地址
     */
    private String indexUrl;

    /**
     * 分享标题
     */
    private String shareTitle;

    /**
     * 分享描述
     */
    private String shareDescription;



//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//
//    public String getImgUrl() {
//        return imgUrl;
//    }
//
//    public void setImgUrl(String imgUrl) {
//        this.imgUrl = imgUrl;
//    }
//
//    public Long getSisId() {
//        return sisId;
//    }
//
//    public void setSisId(Long sisId) {
//        this.sisId = sisId;
//    }
//
//    public Long getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Long userId) {
//        this.userId = userId;
//    }
//
//    public boolean isEnableSis() {
//        return enableSis;
//    }
//
//    public void setEnableSis(boolean enableSis) {
//        this.enableSis = enableSis;
//    }
//
//    public String getSisDescription() {
//        return sisDescription;
//    }
//
//    public void setSisDescription(String description) {
//        this.sisDescription = description;
//    }
//
//    public String getIndexUrl() {
//        return indexUrl;
//    }
//
//    public void setIndexUrl(String indexUrl) {
//        this.indexUrl = indexUrl;
//    }
//
//    public String getShareTitle() {
//        return shareTitle;
//    }
//
//    public void setShareTitle(String shareTitle) {
//        this.shareTitle = shareTitle;
//    }
//
//    public String getShareDescription() {
//        return shareDescription;
//    }
//
//    public void setShareDescription(String shareDescription) {
//        this.shareDescription = shareDescription;
//    }
}
