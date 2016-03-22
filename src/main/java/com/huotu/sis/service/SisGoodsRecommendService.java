package com.huotu.sis.service;

import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.SisGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by slt on 2016/3/8.
 */
public interface SisGoodsRecommendService {

    /**
     * 返回店中店的商品
     * @param customerId        商户ID
     * @param user              用户
     * @param title             商品标题用于搜索
     * @param pageable          分页
     * @return
     * @throws Exception
     */
    Page<SisGoods> findSisRecommendGoodsModel(Long customerId, User user, String title, Pageable pageable) throws Exception;

}
