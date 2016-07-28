package com.huotu.sis.service;

import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.SisOpenAwardLog;

/**
 * Created by slt on 2016/2/19.
 */
public interface SisOpenAwardLogService {

    /**
     * 保存开店记录
     * @param customerId        商户ID
     * @param shop            拿开店奖店主
     * @param contribShop     贡献店主
     * @param amount            金额
     * @param srcType           来源(层级)
     * @param orderId           贡献订单号
     * @throws Exception
     */
    SisOpenAwardLog saveSisOpenAwardLog(Long customerId, User shop, User contribShop, Double amount,Integer srcType, String orderId) throws Exception;

}
