package com.huotu.sis.repository;


import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.IndirectPushFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lgh on 2016/5/9.
 */
@Repository
public interface IndirectPushFlowRepository extends JpaRepository<IndirectPushFlow, Long> {
    Long countByTotalGenerationTwoUser(User user);
}
