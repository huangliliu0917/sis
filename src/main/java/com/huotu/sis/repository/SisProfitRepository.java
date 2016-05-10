package com.huotu.sis.repository;

import com.huotu.sis.entity.SISProfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by jinzj on 2016/5/9.
 */
@Repository
public interface SisProfitRepository extends JpaSpecificationExecutor<SISProfit>, JpaRepository<SISProfit, Long> {

    List<SISProfit> findByMerchant_Id(Long merchantId);
}
