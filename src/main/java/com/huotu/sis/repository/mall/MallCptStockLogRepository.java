package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.MallCptStockLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by slt on 2016/2/1.
 *
 * @since 1.3
 */
@Repository
public interface MallCptStockLogRepository extends JpaRepository<MallCptStockLog, Long> {
    List<MallCptStockLog> findByMemberId(Long userId);
}
