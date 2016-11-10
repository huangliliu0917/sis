package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.base.data.JpaSpecificationExecutor;
import com.huotu.huobanplus.common.entity.MallPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by jinzj on 2016/4/15.
 * @version 1.4.2
 */
@Repository
public interface MallPromotionRepository  extends JpaRepository<MallPromotion, Long>, JpaSpecificationExecutor<MallPromotion> {
}
