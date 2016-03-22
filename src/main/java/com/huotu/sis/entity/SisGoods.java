/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.entity;

import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.Merchant;
import com.huotu.huobanplus.common.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.Description;

import javax.persistence.*;

/**
 * 店中店商品，店中店商品同样也是一个商品。
 *
 * <p>同时它将指示更多的数据表示它的状态，比如是否已下架，是否是一个自有商品</p>
 *
 * @author CJ
 */
@Entity
@Table(name = "SIS_SisGoods")
@Getter
@Setter
@Cacheable(value = false)
public class SisGoods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Description("主键，rest不可见")
    private Long id;
    @ManyToOne
    @Description("该店中店商品的店主")
    private User user;

    @ManyToOne
    @Description("商家")
    private Merchant merchant;

    @ManyToOne
    @Description("关联的实际商品")
    private Goods goods;
    @Description("是否已上架")
    private boolean selected;
    @Description("是否已删除")
    private boolean deleted;
    /**
     * 降序排序
     *
     * 每次请求置顶时，则设置该值为聚合max+1
     */
    @Description("排序权重")
    private int orderWeight = 50;
}
