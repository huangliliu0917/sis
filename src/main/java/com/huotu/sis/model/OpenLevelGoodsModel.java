package com.huotu.sis.model;

import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.sis.entity.SisLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by slt on 2016/4/6.
 * 店中店等级对应商品
 */
@Getter
@Setter
public class OpenLevelGoodsModel {
    Integer levelNo;

    SisLevel sisLevel;

    Goods goods;

}
