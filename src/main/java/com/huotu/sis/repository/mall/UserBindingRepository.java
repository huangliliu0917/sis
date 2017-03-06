/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.UserBinding;
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
public interface UserBindingRepository extends JpaRepository<UserBinding, Long> {

    // 删除
    @Override
    void delete(UserBinding entity);

    @Override
    void deleteInBatch(Iterable<UserBinding> entities);

    @Override
    void deleteAllInBatch();

    //保存
    @Override
    <S extends UserBinding> List<S> save(Iterable<S> entities);

    @Override
    <S extends UserBinding> S save(S entity);

    //获取
    @Override
    UserBinding findOne(Long aLong);

    @Override
    Page<UserBinding> findAll(Pageable pageable);

    @Override
    UserBinding getOne(Long aLong);

    @Override
    List<UserBinding> findAll(Iterable<Long> longs);

    @Override
    List<UserBinding> findAll(Sort sort);

    @Override
    List<UserBinding> findAll();
}
