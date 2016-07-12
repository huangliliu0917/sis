package com.huotu.sis.service;

import com.huotu.huobanplus.common.entity.OrderItems;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.model.sis.SimpleSisLevelModel;
import com.huotu.sis.model.sis.SisLevelConditionsModel;
import com.huotu.sis.model.sisweb.OpenLevelGoodsModel;

import java.util.List;
import java.util.Map;

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
     * 根据用户下线开店的数量获取可升级的店铺等级
     * @param user
     * @return
     * @throws Exception
     */
    SisLevel getSisLevelByOfflineSisNum(User user) throws Exception;


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
     * 根据输入用户的开店数量升级
     * @param user      用户
     * @throws Exception
     */
    void upgradeSisLevelByUpShopNum(User user) throws Exception;

    /**
     * 根据等级的升级条件来升级输入用户的店铺等级
     * @return 是否升级成功
     * @throws Exception
     */
    boolean upgradeSisLevelByConditions(User user) throws Exception;

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

    /**
     * 返回等级model列表
     * @param customerId    商户ID
     * @return
     */
    List<SimpleSisLevelModel> getSimpleSisLevelModel(Long customerId);

    /**
     * 返回等级model列表
     * @param customerId    商户ID
     * @return
     */
    List<SimpleSisLevelModel> getSimpleSisLevelModel(Long customerId,Integer LTlevelNo);

    /**
     *  根据店铺等级获取该等级升级的条件model
     * @return
     */
    List<SisLevelConditionsModel> getSisLevelConditionsModels(SisLevel sisLevel);

    /**
     * 获取用户下线各个等级的开店数量
     * @param user  用户
     * @return map的key为店铺等级ID，val为该等级的开店人数，(key为0，表示所有的开店人数)
     */
    Map<Long,Integer> getEachSisLevelNum(User user);

    /**
     * 判断该用户是否能够达到升级标准
     * @param user          升级的店铺用户
     * @param upSisLevel    是否能达到的店铺等级
     * @return
     */
    boolean canToUpgradeSisLevel(User user,SisLevel upSisLevel);


    /**
     * 店铺升级
     * @param user
     */
    void upgradeSisLevel(User user) throws Exception;
}
