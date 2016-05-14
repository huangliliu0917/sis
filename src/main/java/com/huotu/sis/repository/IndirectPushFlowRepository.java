package com.huotu.sis.repository;


import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.IndirectPushFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by lgh on 2016/5/9.
 */
@Repository
public interface IndirectPushFlowRepository extends JpaRepository<IndirectPushFlow, Long> {
    Long countByTotalGenerationTwoUser(User user);

    /**
     * 统计今日总数
     * @param user 用户
     * @param time 今日凌晨时间
     * @return
     */
    @Query("select count(flow) from IndirectPushFlow flow where flow.totalGenerationTwoUser=?1 and flow.time>=?2")
    Long countTodayByTotalGenerationTwoUser(User user, Long time);
}
