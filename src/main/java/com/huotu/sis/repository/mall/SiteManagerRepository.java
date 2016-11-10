package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.SiteManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by helloztt on 2016/10/8.
 * @since 1.6
 */
@Repository
public interface SiteManagerRepository extends JpaRepository<SiteManager,Long>{

    SiteManager findByMerchantIdAndIsOpenTrue(Long merchantId);
}
