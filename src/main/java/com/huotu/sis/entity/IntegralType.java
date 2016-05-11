/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.entity;

/**
 *
 * <p>积分类型</p>
 *
 * @author shiliting
 */
public enum  IntegralType {

    open("开店返利",700),

    upgrade("补差价升级店铺返利",701);

    private String name;
    private int index;


    // 构造方法
    private IntegralType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    // get set 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


}
