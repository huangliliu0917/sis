package com.huotu.sis.entity.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Administrator on 2016/1/25.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenGoodsIdLevelId {
    /**
     * 等级id
     */
    @JsonProperty("levelid")
    private long levelid;

//    /**
//     * 等级名称
//     */
//    @JsonProperty("levelname")
//    private String levelname;

    /**
     * 商品id
     */
    @JsonProperty("goodsid")
    private long goodsid;

//    /**
//     * 商品名称
//     */
//    @JsonProperty("goodsname")
//    private String goodsname;
//
//
//    /**
//     * 商品价格
//     */
//    @JsonProperty("goodsprice")
//    private double goodsprice;
}
