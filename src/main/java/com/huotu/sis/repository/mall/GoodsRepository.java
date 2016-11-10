/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.base.data.JpaSpecificationExecutor;
import com.huotu.huobanplus.common.entity.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Collection;
import java.util.List;

/**
 * @author CJ
 */
@RepositoryRestResource
public interface GoodsRepository extends JpaRepository<Goods, Long>, JpaSpecificationExecutor<Goods> {

    @Query("select goods from Goods goods where goods.id in ?1 and goods.owner.id = ?2")
    List<Goods> findByIdIns(Collection<Long> ids, Long customerId);


    @Query("select g from Goods as g inner join SisGoodsRecommend as sg on " +
            "g.id=sg.goodsId where sg.customerId=?1 order by sg.sortNo desc ,sg.id desc ")
    Page<Goods> findSisGoodsRecommendList(Long customerId, Pageable pageable);

    @Query("select g.id,g.title,g.price,g.stock,sg.id from Goods as g left join SisGoodsRecommend as sg " +
            "on g.id=sg.goodsId where g.owner.id=?1 and g.title like ?2" +
            " and g.disabled=false and g.marketable=true and g.scenes=0 and g.saleChannels in(0,2)")
    Page<Goods> findByOwnerAndTitleLike(Long merchantId, String title, Pageable pageable);
}
