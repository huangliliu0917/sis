package com.huotu.sis.repository;

import com.huotu.sis.entity.SisConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by lgh on 2015/11/9.
 */

@Repository
public interface SisConfigRepository extends JpaRepository<SisConfig, Long> {
//    @Query("select s from SisConfig as s where s.merchantId=?1")
//    SisConfig findByMerchantId(Long merchantId);

    SisConfig findByMerchantId(@Param("merchantId") Long merchantId);

//    @Query("select sc from SisConfig as sc where sc.merchantId=?1")
//    SisConfig findByMerchantId(Long merchantId);

}
