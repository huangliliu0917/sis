package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.MallCoupons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by jinzj on 2016/5/4.
 */
@Repository
public interface MallCouponsRepository extends JpaRepository<MallCoupons, Long>, JpaSpecificationExecutor<MallCoupons> {
}
