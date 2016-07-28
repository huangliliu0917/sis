package com.huotu.sis.service;

import com.huotu.huobanplus.common.entity.MallCptStockLog;
import com.huotu.huobanplus.common.entity.User;

/**
 * Created by Administrator on 2016/2/1.
 */
public interface MallCptStockLogService {


    /**
     * 保存送股份流水
     *
     * @param customerId       商户ID
     * @param cumulativeAmount 比例
     * @param stockNum         送的股数
     * @param member           会员ID
     * @param contribMember    贡献会员ID
     * @param orderId          订单号
     * @return
     * @throws Exception
     */
    MallCptStockLog saveCptStockLogs(Long customerId, Double cumulativeAmount, Integer stockNum, User member, User contribMember, String orderId) throws Exception;
}
