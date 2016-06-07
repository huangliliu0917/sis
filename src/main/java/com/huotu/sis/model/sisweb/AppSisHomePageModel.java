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
 * 店中店首页模型
 * Created by slt on 2015/11/5.
 */
@Getter
@Setter
public class AppSisHomePageModel {

    /**
     * 店中店名称
     */
    private String sisTitle;

    /**
     * 店中店图片
     */
    private String sisImg;



    /**
     * 店中店二维码
     */
    private String qRCode;

//    public String getSisTitle() {
//        return sisTitle;
//    }
//
//    public void setSisTitle(String sisTitle) {
//        this.sisTitle = sisTitle;
//    }
//
//    public String getSisImg() {
//        return sisImg;
//    }
//
//    public void setSisImg(String sisImg) {
//        this.sisImg = sisImg;
//    }
//
//    public String getqRCode() {
//        return qRCode;
//    }
//
//    public void setqRCode(String qRCode) {
//        this.qRCode = qRCode;
//    }
}
