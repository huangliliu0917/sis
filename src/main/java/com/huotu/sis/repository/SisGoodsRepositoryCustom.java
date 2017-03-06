/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.repository;

import com.huotu.huobanplus.common.entity.Brand;
import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.SisGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author CJ
 */
public interface SisGoodsRepositoryCustom {

    /**
     * 获取该店中店店主所有<strong>可</strong>上架商品
     *
     * @param user     店中店店主
     * @param pageable 分组依据
     * @return 商品
     */
    @Description("获取该店中店店主所有的可上架商品")
    Page<SisGoods> findUserAllSISGoods(@RequestParam("user")
                                       @Param("user")
                                       User user, Pageable pageable);


    /**
     * 获取该店中店<strong>可</strong>上架商品
     *
     * @param user     店主
     * @param catId    分类ID
     * @param pageable 分页数据
     * @param key      搜索关键字
     * @return
     */
    @Description("获取该店中店店主分类之后的所有的可上架商品")
    List<SisGoods> findUserSISGoods(@RequestParam("user") @Param("user") User user,
                                    @RequestParam("catId") @Param("catId") Long catId,
                                    @RequestParam("key") @Param("key") String key,
                                    Pageable pageable);


    /**
     * 展示该小伙伴店中店的商品
     *
     * @param user     小伙伴，店中店店主
     * @param pageable 分组依据
     * @param selected 是否上架
     * @return 销售中商品
     */
//    @Query(
//            "select goods from Goods as goods " +
//                    "left join SisGoods as sis on sis.goods=goods and sis.user=:user " +
//                    "where goods.marketable=true and goods.disabled=false and (sis.deleted=false or sis is null) and sis.selected=:selected " +
//                    "order by sis.orderWeight")
    @Description("展示该小伙伴店中店上架销售商品")
    List<Goods> findSISGoods(@RequestParam("user") @Param("user") User user,
                             @RequestParam("selected") @Param("selected") boolean selected,
                             @RequestParam("key") @Param("key") String key,
                             Pageable pageable);

    @Description("获取店中店商品上架数量和下架数量")
    Long[] sisGoodsTotals(@RequestParam("user") @Param("user") User user);

    @Description("获取该店中店店主分类之后的所有的可上架商品,按照id排序")
    List<SisGoods> findUserSISGoodsOrderById(@RequestParam("user") @Param("user") User user,
                                             @RequestParam("categoryId") @Param("categoryId") Long categoryId,
                                             @RequestParam("name") @Param("name") String name,
                                             Pageable pageable);

    @Description("获取该店中店店主分类之后的所有的可上架商品的总数")
    Integer findUserSISGoodsCount(User user, Long categoryId, String name);

    @Description("获取该店中店店主分类之后的所有的可上架商品的总数")
    Integer findUserSISGoodsCount(User user, String keywords, Brand brand);


}
