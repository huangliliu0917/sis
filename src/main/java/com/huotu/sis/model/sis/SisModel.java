package com.huotu.sis.model.sis;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 店中店查询条件
 * Created by slt on 2016/2/19.
 */
@Getter
@Setter
public class SisModel {
    /**
     *  店铺图片
     */
    private String pic;

    /**
     * 店铺标题
     */
    private String title;

    /**
     * 店铺描述
     */
    private String desc;

    /**
     * 开店小伙伴
     */
    private String userName;

    /**
     * 店铺等级
     */
    private String levelName;

    /**
     * 店铺状态
     */
    private Boolean status;

    /**
     * 分享标题
     */
    private String shareTitle;

    /**
     * 分享描述
     */
    private String shareDesc;

    /**
     * 开店时间
     */
    private Date time;



}
