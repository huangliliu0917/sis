/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.Merchant;
import com.huotu.huobanplus.common.entity.MerchantConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author CJ
 */
@Repository
public interface MerchantConfigRepository extends JpaRepository<MerchantConfig, Long> {



    @Query("select m from MerchantConfig as m where m.merchant.id=?1")
    MerchantConfig findByMerchantId(Long merchantId);

    MerchantConfig findByMerchant(Merchant merchant);

    // 删除
    @Override
    void delete(MerchantConfig entity);

    @Override
    void deleteInBatch(Iterable<MerchantConfig> entities);

    @Override
    void deleteAllInBatch();

    //保存
    @Override
    <S extends MerchantConfig> List<S> save(Iterable<S> entities);

    @Override
    <S extends MerchantConfig> S save(S entity);

    //获取
    @Override
    MerchantConfig findOne(Long aLong);

    @Override
    Page<MerchantConfig> findAll(Pageable pageable);

    @Override
    MerchantConfig getOne(Long aLong);

    @Override
    List<MerchantConfig> findAll(Iterable<Long> longs);

    @Override
    List<MerchantConfig> findAll(Sort sort);

    @Override
    List<MerchantConfig> findAll();
}
