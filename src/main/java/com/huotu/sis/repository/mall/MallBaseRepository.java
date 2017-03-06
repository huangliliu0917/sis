/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.MallBaseConfig;
import com.huotu.huobanplus.common.entity.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author CJ
 */
@Repository
public interface MallBaseRepository extends JpaRepository<MallBaseConfig, Long> {

    MallBaseConfig findByMerchant(Merchant merchant);

    // 删除
    @Override
    void delete(MallBaseConfig entity);

    @Override
    void deleteAllInBatch();

    //保存
    @Override
    <S extends MallBaseConfig> List<S> save(Iterable<S> entities);

    @Override
    <S extends MallBaseConfig> S save(S entity);

    //获取
    @Override
    MallBaseConfig findOne(Long aLong);

    @Override
    Page<MallBaseConfig> findAll(Pageable pageable);

    @Override
    MallBaseConfig getOne(Long aLong);

    @Override
    List<MallBaseConfig> findAll(Iterable<Long> longs);

    @Override
    List<MallBaseConfig> findAll(Sort sort);

    @Override
    List<MallBaseConfig> findAll();
}
