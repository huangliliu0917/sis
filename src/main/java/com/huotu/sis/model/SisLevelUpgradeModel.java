package com.huotu.sis.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jinzj on 2016/4/6.
 */
@Getter
@Setter
public class SisLevelUpgradeModel {

    private Long levelId;
    private String levelName;
    private Integer levelNo;
//    商品地址
    private String goodsUrl;
//    原价
    private Long originalPrice;
//    现价
    private Long currentPrice;
//    图片地址
    private String imgUrl;
}
