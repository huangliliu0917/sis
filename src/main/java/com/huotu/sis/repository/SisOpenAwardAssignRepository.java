package com.huotu.sis.repository;

import com.huotu.sis.entity.SisOpenAwardAssign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2016/3/31.
 */
@Repository
public interface SisOpenAwardAssignRepository extends JpaRepository<SisOpenAwardAssign,Long> {
    SisOpenAwardAssign findByLevel_IdAndGuideLevel_IdAndUserLevel(Long levelId, Long ownLevelId,Long userLevel);

    List<SisOpenAwardAssign> findByMerchant_Id(Long customerId);

}
