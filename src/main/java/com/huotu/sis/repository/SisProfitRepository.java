package com.huotu.sis.repository;

import com.huotu.huobanplus.common.entity.Merchant;
import com.huotu.sis.entity.SISProfit;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.entity.support.ProfitUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by jinzj on 2016/5/9.
 */
@Repository
public interface SisProfitRepository extends JpaSpecificationExecutor<SISProfit>, JpaRepository<SISProfit, Long> {

    List<SISProfit> findByMerchant_Id(Long merchantId);

    @Query("select p.profit from SISProfit p where p.merchant=?1 and p.userLevel=?2 and p.level=?3 and p.profitUser=?4")
    Double getProfitByMerchantAndUserLevelAndSisLevel(Merchant merchant, Long userLevel, SisLevel level,ProfitUser profitUser);

    @Query("select p.profit from SISProfit p where p.merchant=?1 and p.userLevel=?2   and p.profitUser=?3")
    Double getProfitByMerchantAndUserLevel(Merchant merchant, Long userLevel,ProfitUser profitUser);
}
