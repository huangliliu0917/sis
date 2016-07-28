package com.huotu.sis.service;

import com.huotu.huobanplus.common.entity.MallAdvanceLogs;
import com.huotu.huobanplus.common.entity.User;

/**
 * Created by Administrator on 2016/1/29.
 */
public interface MallAdvanceLogsService {
    /**
     * 插入开店奖流水
     * @param earningsUser      受益人
     * @param contributeUser    贡献人
     * @param money             金额
     * @param orderId           贡献订单ID
     * @param unionOrderId      贡献联合订单ID
     * @param srcType           返利的层级
     * @return
     * @throws Exception
     */
    MallAdvanceLogs saveMallAdvanceLogs(User earningsUser, User contributeUser, Double money, String orderId, String unionOrderId, Integer srcType) throws Exception;

}
