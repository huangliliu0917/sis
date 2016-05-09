package com.huotu.sis.service;

import com.huotu.huobanplus.common.entity.OrderItems;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.model.OpenLevelGoodsModel;

import java.util.List;

/**
 * Created by slt on 2016/4/6.
 */
public interface SisLevelService {


    /**
     * 根据货品的数量获取升级的等级
     * @param sisConfig     店中店配置表
     * @param sis           用户店中店
     * @param orderItems    订单详情
     * @param user          用户
     * @return
     * @throws Exception
     */
    SisLevel getUpgradeSisLevel(SisConfig sisConfig,Sis sis,OrderItems orderItems, User user) throws Exception;


    /**
     * 升级店中店等级
     * @param user          所属用户
     * @param sisConfig     店中店配置信息
     * @param orderItems    订单详情
     * @return
     * @throws Exception
     */
    boolean upgradeSisLevel(User user, SisConfig sisConfig, OrderItems orderItems) throws Exception;


    /**
     * 保存店中店等级
     * @param sisLevel      用户保存的等级
     * @param sis           用户店中店
     * @throws Exception
     */
    void saveSisLevel(Sis sis,SisLevel sisLevel) throws Exception;

    /**
     * 获取等级ID对应的商品map
     * @param sisConfig
     * @return
     * @throws Exception
     */
    List<OpenLevelGoodsModel> getSisOpenGoods(SisConfig sisConfig) throws Exception;



    /**
     * 查找某商户比当前等级大的等级list
     * @param levelNo
     * @param customerId
     * @return
     */
    List<SisLevel> getListBelongByLevelId(Integer levelNo, Long customerId);
}
