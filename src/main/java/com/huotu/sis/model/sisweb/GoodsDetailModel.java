package com.huotu.sis.model.sisweb;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by xhk on 2016/1/7.
 * 物品详情
 */
@Getter
@Setter
public class GoodsDetailModel {

    /**
     * 物品ID
     */
    private Long goodsId;

    /**
     * 物品标题
     */
    private String title;

    /**
     * 物品详情
     */
    private String detail;
    /**
     * 价格
     */
    private Double price;
    /**
     * 颜色与规格
     */
    private String description;

    /**
     * 库存量
     */
    private Integer store;

    /**
     * 物品照片
     */
    private String picture;
    /**
     * 是否是上架状态
     * 上架状态：1
     * 下架状态：0
     */
    private Integer isUp;

    /**
     * 分销返利最大值
     */
    private Integer maxRebate;

    /**
     * 分销返利最小值
     */
    private Integer minRebate;

    /**
     * 直推返利
     */
    private Integer directRebate;

    /**
     * 是否是一键铺货
     */
    private boolean shelves;





}
