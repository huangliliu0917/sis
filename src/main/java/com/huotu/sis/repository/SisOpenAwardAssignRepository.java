package com.huotu.sis.repository;

import com.huotu.huobanplus.common.entity.Merchant;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.entity.SisOpenAwardAssign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2016/3/31.
 */
@Repository
public interface SisOpenAwardAssignRepository extends JpaRepository<SisOpenAwardAssign,Long> {
    @Query("select s from SisOpenAwardAssign as s where s.userLevel=?1 and s.level=?2 and s.guideLevel=?3 and s.merchant=?4")
    SisOpenAwardAssign findByLevel_IdAndGuideLevel_IdAndUserLevelAndMerchant(
            Long userLevel, SisLevel level, SisLevel guideLevel, Merchant merchant);

    @Query("select s from SisOpenAwardAssign as s where s.userLevel=?1 and s.guideLevel=?2 and s.merchant=?3")
    SisOpenAwardAssign findByLevel_IdAndGuideLevel_IdAndMerchant(
            Long userLevel,SisLevel guideLevel,Merchant merchant);

    List<SisOpenAwardAssign> findByMerchant_Id(Long customerId);

}
