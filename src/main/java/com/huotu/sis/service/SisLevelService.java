package com.huotu.sis.service;

import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisLevel;

import java.util.List;

/**
 * Created by slt on 2016/4/6.
 */
public interface SisLevelService {


    /**
     * 根据货品的数量获取升级的等级
     * @param numbers   货品数量
     * @return
     * @throws Exception
     */
    SisLevel getUpgradeSisLevel(Integer numbers,User user) throws Exception;

    /**
     * 获取用户店中店的当前等级所对应的商品
     * @param user
     * @return
     * @throws Exception
     */
    Goods getSisShopOpenGoods(User user, SisConfig sisConfig) throws Exception;

    /**
     * 查找某商户比当前等级大的等级list
     * @param levelNo
     * @param customerId
     * @return
     */
    List<SisLevel> getListBelongByLevelId(Integer levelNo, Long customerId);
}
