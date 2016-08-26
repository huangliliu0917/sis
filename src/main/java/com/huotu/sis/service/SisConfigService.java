package com.huotu.sis.service;

import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.support.SisRebateTeamManagerSetting;
import com.huotu.sis.model.sis.CategoryModel;
import com.huotu.sis.model.sis.MallGoodModel;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

/**
 * 商家店中店配置表
 * Created by Administrator on 2016/1/26.
 */
public interface SisConfigService {

    /**
     * 根据customerId查询商户的店中店配置，如果为空则返回一个初始化的商户店中店配置
     * @param customerId
     * @return
     * @throws Exception
     */
    SisConfig initSisConfig(Long customerId) throws Exception;

    /**
     * 保存商户的分享配置
     * @param sisConfig
     * @throws Exception
     */
    void saveShareConfig(SisConfig sisConfig) throws Exception;

    /**
     * 保存商户的基本配置
     * @param sisConfig
     * @throws Exception
     */
    void saveOpenConfig(SisConfig sisConfig) throws Exception;

    /**
     *  获取商品分类
     * @return
     */
    List<CategoryModel> getCategory(Long customerId) throws IOException;


    /**
     * 检索商品(弃用)
     * @param customerId
     * @param catPath
     * @param title
     * @param pageNo
     * @return
     * @throws IOException
     */
    Page<MallGoodModel> getMallGood(Long customerId, String catPath, String title, Integer pageNo) throws IOException;

    /**
     * 根据关键字搜索商品
     * @param customerId        商家ID
     * @param title             商品标题
     * @param pageNo            分页
     * @return
     * @throws IOException
     */
    Page<MallGoodModel> getMallGood(Long customerId, String title, Integer pageNo) throws IOException;

    /**
     * 初始化直推奖配置信息
     * @return
     * @throws Exception
     */
    SisRebateTeamManagerSetting initSisRebateTeamManagerSetting(Long merchantId) throws Exception;


    /**
     * 兼容单一商品转为为等级商品之后商品没有的尴尬
     * @param customerId        商户ID
     * @throws Exception
     */
    void compatibilityOpenShopGoods(Long customerId) throws Exception;


    /**
     * 兼容单一等级转换为等级商品之后没有选中的尴尬
     * @param customerId    商户ID
     * @throws Exception
     */
    void defaultLevelSelected(Long customerId) throws Exception;

    /**
     * 兼容单一等级转换为等级商品之后没有商品和没有选中的尴尬
     * @param customerId
     * @throws Exception
     */
    void compatibilityOpenShopGoodsAndSelected(Long customerId) throws Exception;

}
