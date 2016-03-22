package com.huotu.sis.service;

import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.sis.entity.SisGoods;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by jinzj on 2016/1/18.
 * @since 1.3
 */
public interface SisGoodsService {

    /**
     * 查找某个店铺的商品，可分页，可排序
     *
     * @param customerId
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    Page<SisGoods> getSisGoodsList(Long customerId, Long userId, int page, int pageSize);

    /**
     * 查找某个店铺的所有上架商品
     * @param customerId
     * @param userId
     * @return
     */
    List<SisGoods> getSisGoodsList(Long customerId, Long userId);

    /**
     * 某个店铺上架的商品数量
     * @param customerId
     * @param userId
     * @return
     */
    Long countByUserId(Long customerId, Long userId);

    /**
     * 获取商品列表
     * @param merchantId
     * @param title
     * @param catPath
     * @param brandId
     * @param sortOption 0:直推返利
     *                   1:商品人气
     * @param page
     * @param pageSize
     * @return
     */
    Page<Goods> getAllGoodsByCustomerIdAndTitleAndCatPath(Long merchantId, String title, Long catPath, Long brandId,
                                                          Integer sortOption, int page, int pageSize);

    /**
     * 获取店中店用户商品列表
     * @param goodsId: 商户商品id
     * @param userId: 用户id
     * @param merchantId: 商户id
     * @return
     */
    List<SisGoods> getAllSisGoodsList(Long goodsId, Long userId, Long merchantId);

}
