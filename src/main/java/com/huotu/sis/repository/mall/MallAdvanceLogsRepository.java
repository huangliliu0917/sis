package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.MallAdvanceLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by slt on 2016/1/29.
 * @since 1.3
 */
@RepositoryRestResource
public interface MallAdvanceLogsRepository extends JpaRepository<MallAdvanceLogs,Long> {
}
