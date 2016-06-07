package com.huotu.sis.model.sis;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by slt on 2016/4/18.
 */
@Getter
@Setter
public class GoodsModel {
    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsTitle;

    /**
     * 商品价格
     */
    private Double goodsPrice;
}
