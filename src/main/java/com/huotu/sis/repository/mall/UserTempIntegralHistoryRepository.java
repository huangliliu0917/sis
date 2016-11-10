package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.UserTempIntegralHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by jinzj on 2016/2/1.
 * @since 1.3
 */
@Repository
public interface UserTempIntegralHistoryRepository extends JpaRepository<UserTempIntegralHistory, Long>,
        JpaSpecificationExecutor<UserTempIntegralHistory> {
}
