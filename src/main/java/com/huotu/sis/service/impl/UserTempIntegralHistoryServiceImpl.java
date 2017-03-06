package com.huotu.sis.service.impl;

import com.huotu.huobanplus.common.entity.UserTempIntegralHistory;
import com.huotu.huobanplus.common.model.analytics.DateAndCountInfo;
import com.huotu.sis.repository.mall.UserTempIntegralHistoryRepository;
import com.huotu.sis.service.UserTempIntegralHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jinzj on 2016/2/1.
 */
@Service
public class UserTempIntegralHistoryServiceImpl implements UserTempIntegralHistoryService {

    @PersistenceContext(unitName = "entityManager")
    private EntityManager entityManager;

    @Autowired
    private UserTempIntegralHistoryRepository userTempIntegralHistoryRepository;

    @Override
    public Long getCountByUserId(Long userId, int type) {
        return userTempIntegralHistoryRepository.count(((root, query, cb) -> {
            Predicate predicate = cb.and(
                    cb.equal(root.get("userId").as(Long.class), userId),
                    cb.equal(root.get("newType").as(Integer.class), type),
                    cb.equal(root.get("positiveFlag").as(Integer.class), 1)
            );
            return predicate;
        }));
    }

    @Override
    public Page<UserTempIntegralHistory> getPageByUserId(Long userId, Integer status, Integer type, int page, int pageSize, Sort sort) {
        Pageable pageable = new PageRequest(page, pageSize, sort);
        return userTempIntegralHistoryRepository.findAll(((root, query, cb) -> {
            Predicate predicate = cb.and(
                    cb.equal(root.get("userId").as(Long.class), userId)
            );
            if (null != type)
                predicate = cb.and(predicate, cb.equal(root.get("newType").as(Integer.class), type));

            if (null != status)
                predicate = cb.and(predicate, cb.equal(root.get("positiveFlag").as(Integer.class), status));
            return predicate;
        }), pageable);
    }

    public List<UserTempIntegralHistory> getListByUserIdAndDate(Long userId, Integer positiveFlag, Integer newType, Date startDate, Date endDate) {

        return userTempIntegralHistoryRepository.findAll(((root, query, cb) -> {
            Predicate predicate = cb.and(
                    cb.equal(root.get("userId").as(Long.class), userId)
            );
            if (null != newType)
                predicate = cb.and(predicate, cb.equal(root.get("newType").as(Integer.class), newType));

            if (null != positiveFlag)
                predicate = cb.and(predicate, cb.equal(root.get("positiveFlag").as(Integer.class), positiveFlag));

            if (null != startDate && null != endDate)
                predicate = cb.and(predicate, cb.between(root.get("addTime").as(Date.class), startDate, endDate));

            return predicate;
        }));

    }

    @Override
    public List<DateAndCountInfo> totalTempIntegralByCustomerIdAndDateBetween(Long customerId, Date startDate, Date endDate) {
        // select YEAR(UTIH_AddTime) AS 'year',MONTH(UTIH_AddTime) AS 'month',DAY(UTIH_AddTime) AS 'day',sum(UTIH_Integral) AS 'total'
        // from dbo.Hot_UserTempIntegral_History
        // where UTIH_CustomerID=4471 AND UTIH_PositiveFlag=1 AND UTIH_AddTime>'2016-06-10' AND UTIH_AddTime<'2016-08-25'
        // group by YEAR(UTIH_AddTime),MONTH(UTIH_AddTime),DAY(UTIH_AddTime)
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        //from
        Root<UserTempIntegralHistory> tempIntegralHistoryRoot = cq.from(UserTempIntegralHistory.class);
        //定义year(),month(),day()
        List<Expression> groupByList = new ArrayList<>();
        Expression<Integer> yearExp = cb.function("year", Integer.class, tempIntegralHistoryRoot.get("addTime"));
        Expression<Integer> monthExp = cb.function("month", Integer.class, tempIntegralHistoryRoot.get("addTime"));
        Expression<Integer> dayExp = cb.function("day", Integer.class, tempIntegralHistoryRoot.get("addTime"));
        Expression<Double> totalIntegralExp = cb.sumAsDouble(tempIntegralHistoryRoot.get("integral"));
        groupByList.add(yearExp);
        groupByList.add(monthExp);
        groupByList.add(dayExp);
        //select
        cq.select(cb.construct(DateAndCountInfo.class, yearExp.alias("year"), monthExp.alias("month"), dayExp.alias("day"), totalIntegralExp.alias("countNum")));
        //where
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(cb.equal(tempIntegralHistoryRoot.get("customerId").as(Long.class), customerId));
        predicateList.add(cb.equal(tempIntegralHistoryRoot.get("positiveFlag").as(Integer.class), 1));
        predicateList.add(cb.greaterThanOrEqualTo(tempIntegralHistoryRoot.get("addTime").as(Date.class), startDate));
        predicateList.add(cb.lessThan(tempIntegralHistoryRoot.get("addTime").as(Date.class), endDate));
        cq.where(predicateList.toArray(new Predicate[predicateList.size()]));
        //group by
        cq.groupBy(groupByList);
        List<DateAndCountInfo> infoList = entityManager.createQuery(cq).getResultList();
        return infoList;
    }
}
