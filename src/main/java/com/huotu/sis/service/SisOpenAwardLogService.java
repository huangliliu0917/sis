package com.huotu.sis.service;

import com.huotu.sis.entity.SisOpenAwardLog;

/**
 * Created by slt on 2016/2/19.
 */
public interface SisOpenAwardLogService {

    /**
     * 保存店中店开店，返利等记录
     * @param customerId        商户ID
     * @param shopId            拿奖店主Id
     * @param contribShopId     贡献店主Id
     * @param amount            金额
     * @param remark            备注
     * @param openType          开店类型
     * @param srcType           来源(层级)
     * @param orderId           贡献订单号
     * @throws Exception
     */
    SisOpenAwardLog saveSisOpenAwardLog(Long customerId, Long shopId, Long contribShopId, Double amount,
                                        String remark, Integer srcType,Integer openType, String orderId) throws Exception;

}
