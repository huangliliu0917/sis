/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.Category;
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
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("select c from Category as c where c.owner.id=?1")
    List<Category> findByCustomerId(Long customerId);
    // 删除
    @Override
    void delete(Category entity);

    //保存
    @Override
    <S extends Category> List<S> save(Iterable<S> entities);

    @Override
    <S extends Category> S save(S entity);

    //获取
    @Override
    Category findOne(Long aLong);

    @Override
    Page<Category> findAll(Pageable pageable);

    @Override
    Category getOne(Long aLong);

    @Override
    List<Category> findAll(Iterable<Long> longs);

    @Override
    List<Category> findAll(Sort sort);

    @Override
    List<Category> findAll();

}
