package com.huotu.sis.model.sis;

import lombok.Getter;
import lombok.Setter;

/**
 * 简单的商品model
 * Created by slt on 2016/2/19.
 */
@Getter
@Setter
public class SimpleGoodsModel {
    /**
     *  商品ID
     */
    private Long id;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 价格
     */
    private Double price;

    /**
     * 是否为店中店推荐商品,0是，1不是
     */
    private Integer isRecommend;




}
