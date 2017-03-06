/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.GoodsImage;
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
public interface GoodsImageRepository extends JpaRepository<GoodsImage, Long> {

    List<GoodsImage> findByGoods(@Param("goods") Goods goods);

    // 删除
    @Override
    void delete(GoodsImage entity);

    @Override
    void deleteInBatch(Iterable<GoodsImage> entities);

    @Override
    void deleteAllInBatch();

    //保存
    @Override
    <S extends GoodsImage> List<S> save(Iterable<S> entities);

    @Override
    <S extends GoodsImage> S save(S entity);

    //获取
    @Override
    GoodsImage findOne(Long aLong);

    @Override
    Page<GoodsImage> findAll(Pageable pageable);

    @Override
    GoodsImage getOne(Long aLong);

    @Override
    List<GoodsImage> findAll(Iterable<Long> longs);

    @Override
    List<GoodsImage> findAll(Sort sort);

    @Override
    List<GoodsImage> findAll();
}
