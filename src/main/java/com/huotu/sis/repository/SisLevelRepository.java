package com.huotu.sis.repository;

import com.huotu.sis.entity.SisLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lgh on 2015/11/9.
 */

@Repository
public interface SisLevelRepository extends JpaRepository<SisLevel, Long>,JpaSpecificationExecutor<SisLevel> {
    @Query("select sl from SisLevel as sl where sl.merchantId=?1 and sl.isSystem=1")
    SisLevel findByMerchantIdSys(Long merchantId);


    List<SisLevel> findByMerchantId(Long customerId);

    @Query("select sl from SisLevel as sl where sl.id in ?1")
    List<SisLevel> findByIdIn(List<Long> ids);

    SisLevel findFirstByMerchantIdOrderByLevelNoDesc(Long merchantId);

    List<SisLevel> findAll();
    List<SisLevel> findByMerchantIdOrderByLevelNoAsc(Long customerId);
    List<SisLevel> findByMerchantIdOrderByLevelNoDesc(Long customerId);

    @Query("select max(l.levelNo) from SisLevel as l where l.merchantId=?1")
    Object findTopSisLevel(Long customerId);


}
