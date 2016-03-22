/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.repository;

import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.Merchant;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.SisGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author CJ
 */
@RepositoryRestResource(
        collectionResourceDescription = @Description("店中店商品关联信息集合"),
        itemResourceDescription = @Description("店中店商品关联信息")
)
public interface SisGoodsRepository extends JpaRepository<SisGoods, Long>,JpaSpecificationExecutor<SisGoods>, SisGoodsRepositoryCustom {

    SisGoods findByGoodsAndUser(Goods goods, User user);

    @RestResource(exported = false)
    @Query(
            "select goods,sis from Goods as goods " +
                    "left join SisGoods as sis on sis.goods=goods and sis.user=:user " +
                    "where goods.owner=:merchant and goods.marketable=true and goods.disabled=false and (sis.deleted=false or sis is null) " +
                    "order by goods.orderWeight")
    Page findAllSISGoods(
            @RequestParam("user")
            @Param("user")
            User user, @RequestParam("merchant")
            @Param("merchant")
            Merchant merchant, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update SisGoods as g set g.selected=false where g.user=:user and g.merchant=:merchant and g.selected=true and g.deleted=false")
    int soldOutSisGoods(@RequestParam("user")
                        @Param("user")
                        User user, @RequestParam("merchant")
                        @Param("merchant")
                        Merchant merchant);


//    /**
//     * 按照分类查找可上架商品
//     * @param user      店主ID
//     * @param merchant  商家
//     * @param catId     分类ID
//     * @param pageable  分页
//     * @return    分类后的可上架商品
//     */
//    @RestResource(exported = false)
//    @Query(
//            "select goods,sis from Goods as goods " +
//                    "left join SisGoods as sis on sis.goods=goods and sis.user=:user " +
//                    "left join Category as cat on goods.category=cat " +
//                    "where goods.owner=:merchant and goods.marketable=true and goods.disabled=false and (sis.deleted=false or sis is null) " +
//                    "and cat.id=:catId "+
//                    "order by goods.orderWeight"
//    )
//    Page findSISGoodsByCat(
//            @RequestParam("user")
//            @Param("user")
//            User user, @RequestParam("merchant")
//            @Param("merchant")
//            Merchant merchant,
//            @RequestParam("catId")
//            @Param("catId")
//            Long catId,Pageable pageable);









    @Description("获取小伙伴店中店权重最高商品的值")
    @Query("select max(sis.orderWeight) from SisGoods as sis where sis.user=:user")
    int     getTopSisGoods(@RequestParam("user") @Param("user") User user);

}
