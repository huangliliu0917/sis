package com.huotu.sis.model.sisweb;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by slt on 2016/1/26.
 */
@Getter
@Setter
public class SisLevelModel {
    /**
     * 等级id
     */
    private Long levelId;

    /**
     * 等级名称
     */
    private String levelTitle;

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
