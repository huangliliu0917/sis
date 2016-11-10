package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.UserFormalIntegral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by slt on 2016/5/9.
 * @since 1.5
 */
@Repository
public interface UserFormalIntegralRepository extends JpaRepository<UserFormalIntegral,Long>,
        JpaSpecificationExecutor<UserFormalIntegral> {
}
