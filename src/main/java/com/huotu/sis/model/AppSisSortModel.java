/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 店中店分类模型
 * Created by slt on 2015/11/5.
 */
@Getter
@Setter
public class AppSisSortModel {

    /**
     * 分类ID
     */
    private Long sisId;

    /**
     * 分类名称
     */
    private String sisName;

    /**
     * 下一级分类
     */
    List<AppSisSortModel> list ;

//    public Long getSisId() {
//        return sisId;
//    }
//
//    public void setSisId(Long sisId) {
//        this.sisId = sisId;
//    }
//
//    public String getSisName() {
//        return sisName;
//    }
//
//    public void setSisName(String sisName) {
//        this.sisName = sisName;
//    }
//
//    public List<AppSisSortModel> getList() {
//        return list;
//    }
//
//    public void setList(List<AppSisSortModel> list) {
//        this.list = list;
//    }
}
