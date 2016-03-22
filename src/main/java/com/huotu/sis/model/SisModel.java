package com.huotu.sis.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by xhk on 2016/1/8.
 * 店中店model
 */
@Getter
@Setter
public class SisModel {
    /**
     * 店铺的路径
     */
    private String head;
    /**
     * 头像相对路径
     */
    private String relativeUrl;
    /**
     * 店铺的名字
     */
    private String sisName;

    /**
     * 店铺简介
     */
    private String sisDetail;

    /**
     * 分享标题
     *
     */
    private String shareTitle;

    /**
     * 分享内容
     */
    private String shareDetail;
}
