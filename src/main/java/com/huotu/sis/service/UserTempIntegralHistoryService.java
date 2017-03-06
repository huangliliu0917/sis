package com.huotu.sis.service;

import com.huotu.huobanplus.common.entity.UserTempIntegralHistory;
import com.huotu.huobanplus.common.model.analytics.DateAndCountInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;

/**
 * Created by jinzj on 2016/2/1.
 *
 * @since 1.3
 */
public interface UserTempIntegralHistoryService {

    /**
     * 某个店主的订单数
     *
     * @param userId
     * @param type
     * @return
     */
    Long getCountByUserId(Long userId, int type);

    Page<UserTempIntegralHistory> getPageByUserId(Long userId, Integer status, Integer type, int page, int pageSize, Sort sort);

    /**
     * 根据positiveFlag和newType和时间查询返利流水
     *
     * @param userId       用户id
     * @param positiveFlag 1：动态，实时监控和更新，0：流水账
     * @param newType      500：店中店
     * @param startDate
     * @param endDate
     * @return
     */
    List<UserTempIntegralHistory> getListByUserIdAndDate(Long userId, Integer positiveFlag, Integer newType, Date startDate, Date endDate);

    /**
     * 根据商户ID统计时间段内返利
     *
     * @param customerId
     * @param startDate
     * @param endDate
     * @return
     * @since 1.5.3
     */
    List<DateAndCountInfo> totalTempIntegralByCustomerIdAndDateBetween(Long customerId, Date startDate, Date endDate);
}
