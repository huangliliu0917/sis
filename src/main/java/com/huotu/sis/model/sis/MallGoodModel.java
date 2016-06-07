package com.huotu.sis.model.sis;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Administrator on 2015/12/22.
 */
@Getter
@Setter
public class MallGoodModel {
    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品名
     */
    private String title;

    /**
     * 价格
     */
    private Double price;

    /**
     * 所属分类的路径
     */
    private String path;

    /**
     * 原价
     */
    private Double originalPrice;

    /**
     * 图片
     */
    private String img;

    /**
     * 可获取的积分
     */
    private Integer integral;
}
