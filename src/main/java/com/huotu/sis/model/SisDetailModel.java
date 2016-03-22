package com.huotu.sis.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jinzj on 2016/2/23.
 */
@Getter
@Setter
public class SisDetailModel {
    /**
     *  店铺id
     */
    private Long sisId;
    /**
     * 店铺名字
     */
    private String sisName;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 开店时间，序列化，以"yyyy-MM-dd HH:mm:ss"格式
     */
    private String startDate;
    /**
     * 开店奖
     */
    private double openPrice;
    /**
     * 微信头像
     */
    private String weixinImageUrl;

    private String realName;

    private Long mobile;
}
