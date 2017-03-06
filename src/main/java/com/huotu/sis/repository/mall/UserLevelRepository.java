/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.UserType;
import com.huotu.huobanplus.common.entity.UserLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author CJ
 */
@Repository
public interface UserLevelRepository extends JpaRepository<UserLevel, Long> {

    /**
     * 根据商户id 和 等级类型 获取会员级别
     *
     * @param id
     * @param type
     * @param pageable
     * @return
     */
    Page<UserLevel> findByMerchant_IdAndTypeOrderByLevelAsc(
            @Param("id")
            Long id,
            @Param("type")
            UserType type,
            Pageable pageable
    );

    Page<UserLevel> findByMerchant_IdOrderByLevelAsc(
            @Param("id")
            Long id,
            Pageable pageable
    );

    /**
     * 安装等级排序，获取级别列表
     *
     * @param id 商家Id
     * @param type 用户类型
     * @return
     */
    List<UserLevel> findAllByMerchant_IdAndTypeOrderByLevelAsc(Long id, UserType type);

    // 删除
    @Override
    void delete(UserLevel entity);

    @Override
    void deleteInBatch(Iterable<UserLevel> entities);

    @Override
    void deleteAllInBatch();

    //保存
    @Override
    <S extends UserLevel> List<S> save(Iterable<S> entities);

    @Override
    <S extends UserLevel> S save(S entity);

    //获取
    @Override
    UserLevel findOne(Long aLong);

    @Override
    Page<UserLevel> findAll(Pageable pageable);

    @Override
    UserLevel getOne(Long aLong);

    @Override
    List<UserLevel> findAll(Iterable<Long> longs);

    @Override
    List<UserLevel> findAll(Sort sort);

    @Override
    List<UserLevel> findAll();
}
