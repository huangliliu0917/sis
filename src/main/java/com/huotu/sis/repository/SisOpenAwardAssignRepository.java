package com.huotu.sis.repository;

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
    @Query("select s from SisOpenAwardAssign as s where s.userLevel=?1 and s.level=?2 and s.guideLevel=?3")
    SisOpenAwardAssign findByLevel_IdAndGuideLevel_IdAndUserLevel(Long userLevel,SisLevel level,SisLevel guideLevel);

    @Query("select s from SisOpenAwardAssign as s where s.userLevel=?1 and s.level=?2")
    SisOpenAwardAssign findByLevel_IdAndGuideLevel_Id(Long userLevel,SisLevel level);

    List<SisOpenAwardAssign> findByMerchant_Id(Long customerId);

}
