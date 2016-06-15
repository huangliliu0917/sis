package com.huotu.sis.model.sisweb;

import com.huotu.huobanplus.common.entity.User;
import lombok.Getter;
import lombok.Setter;

/**
 * 临时积分表model
 * Created by admin on 2016/1/31.
 * modify  by slt on 201616/13
 */
@Setter
@Getter
public class UserTempIntegralHistoryModel {

    /**
     * 返利用户
     */
    private User user;

    /**
     * 贡献用户
     */
    private User contributeUser;

    /**
     * 返利积分
     */
    private int integral;

    /**
     * 直推比例
     */
    private double pushRatio;
//
//    /**
//     * 预计转正天数
//     */
//    private int positiveDay;
//
//    /**
//     * 订单
//     */
//    private Order order;
//
//    /**
//     * 联合订单ID
//     */
//    private String unionOrderId;

}
