/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.Description;

import javax.persistence.*;

/**
 *
 * <p>店主专享商品</p>
 *
 * @author shiliting
 */
@Entity
@Table(name = "SIS_Goods_Recommend")
@Getter
@Setter
@Cacheable(value = false)
public class SisGoodsRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RecId")
    @Description("ID")
    private Long id;


    @Column(name = "GoodsId")
    @Description("商品ID")
    private Long goodsId;


    @Description("商户ID")
    @Column(name = "CustomerId")
    private Long customerId;

    @Description("排序ID")
    @Column(name = "SortNo")
    private Long sortNo;


}
