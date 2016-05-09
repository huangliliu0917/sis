package com.huotu.sis.entity;

import com.huotu.huobanplus.common.entity.Order;
import com.huotu.huobanplus.common.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * (总代2)间推流水表
 * <p>
 * 不存储直推的数据
 * Created by lgh on 2016/5/7.
 */

@Entity
@Getter
@Setter
@Cacheable(value = false)
public class IndirectPushFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 当前用户
     */
    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private User owner;

    /**
     * 用户店铺等级类型
     * 1专卖店
     * 2旗舰店
     */
    private Integer userShopLevelType;

    /**
     * 所属间推的总代2用户Id
     */
    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private User totalGenerationTwoUser;

    /**
     * 所属订单
     */
    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private Order order;

    /**
     * 时间
     */
    private Long time;
}
