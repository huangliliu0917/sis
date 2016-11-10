/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.WebSiteAppConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author CJ
 * @since 1.5
 */
@Repository
public interface WebSiteAppConfigRepository extends JpaRepository<WebSiteAppConfig, Long> {

    WebSiteAppConfig findTop1ByMerchant_Id(@Param("merchantId") Long merchantId);

    @Query(value = "select distinct w.merchant.id from WebSiteAppConfig w")
//    @PreAuthorize("hasAnyRole('ROOT','" + ManagerRole + "')")
    List<Long> findDistinctMerchantId();

    @Query(value = "select w.pullicName from WebSiteAppConfig w where w.merchant.id = ?1 and w.appType = 1")
    String findPullicNameByMerchantIdAndAppType(Long merchantId);

}
