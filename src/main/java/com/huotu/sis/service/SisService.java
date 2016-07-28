package com.huotu.sis.service;

import com.huotu.huobanplus.common.entity.Order;
import com.huotu.huobanplus.common.entity.OrderItems;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.entity.UserTempIntegralHistory;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.entity.support.RelationAndPercent;
import com.huotu.sis.entity.support.SisRebateTeamManagerSetting;
import com.huotu.sis.model.sis.SisSearchModel;
import com.huotu.sis.model.sisweb.UserTempIntegralHistoryModel;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by Administrator on 2016/1/29.
 */
public interface SisService {

    /**
     * 获取用户店铺等级ID
     * @param user
     * @return
     * @throws Exception
     */
    long getSisLevelId(User user) throws Exception;

    /**
     * 获取用户店铺的等级
     * @param user
     * @return
     * @throws Exception
     */
    Integer getSisLevel(User user) throws Exception;


    /**
     * 查找店中店的记录
     * @param sisSearchModel    查询model
     * @return
     * @throws Exception
     */
    Page<Sis> findSisList(SisSearchModel sisSearchModel) throws Exception;

    /**
     * 计算直推奖
     * @param user          返利用户
     * @param order         产生返利的订单
     * @param unionOrderId  产生返利的联合订单
     * @throws Exception
     */
    void calculatePushAward(User user, Order order, String unionOrderId, SisConfig sisConfig) throws Exception;

    /**
     * 经营者模式计算直推奖
     * @throws Exception
     */
    List<UserTempIntegralHistoryModel> countProprietor(User user, Order order,SisConfig sisConfig) throws Exception;

    /**
     * 默认直推奖计算
     * @throws Exception
     */
    UserTempIntegralHistoryModel countDefPush(User user, Order order, SisLevel userSisLevel) throws Exception;


    /**
     * 新的默认直推奖计算
     * @param user          店主
     * @param contriUser    贡献用户
     * @param sisConfig     店铺配置信息
     * @return
     * @throws Exception
     */
    List<UserTempIntegralHistoryModel> newCountDefPush(User user, User contriUser,SisConfig sisConfig,Long sislevelId) throws Exception;

    /**
     * 将model转换为实体
     * @param models    临时积分model
     * @return
     * @throws Exception
     */
    List<UserTempIntegralHistory> convertModelToUserTempIntegralHistory(List<UserTempIntegralHistoryModel> models) throws Exception;

    /**
     * 保存临时积分表
     * @param userTempIntegralHistories    保存的实体列表
     * @return
     * @throws Exception
     */
    List<UserTempIntegralHistory> saveUserTempIntegralHistory(List<UserTempIntegralHistory> userTempIntegralHistories) throws Exception;

    /**
     * 保存用户的临时积分
     * @param models        临时积分model
     * @return
     * @throws Exception
     */
    List<User> saveUsersTempIntegral(List<UserTempIntegralHistoryModel> models) throws Exception;


    /**
     * 计算订单总金额
     * @param orderItemses
     * @return
     * @throws Exception
     */
    double countOrderItemsTotalAmount(List<OrderItems> orderItemses);


    /**
     * 计算直推积分
     * @param orderItemses      订单列表
     * @param rebateRate        直推返利比例
     * @param exchangeRate      积分和钱的转换比例
     * @return
     * @throws Exception
     */
    int countTotalIntegral(List<OrderItems> orderItemses,double rebateRate,int exchangeRate);

    /**
     * 计算用户的直推比例
     * @param order     购买的订单
     * @param users     用户列表
     * @param setting   配置信息
     * @return
     * @throws Exception
     */
    List<UserTempIntegralHistoryModel> countUserTempIntegralHistoryModel(Order order,List<User> users, SisRebateTeamManagerSetting setting) throws Exception;

    /**
     * 测试直推比例
     * @return
     * @throws Exception
     */
    List<Double> testUserTempIntegralHistoryModel(List<Integer>levels,List<RelationAndPercent> manageAwards) throws Exception;

    /**
     * 根据等级序号计算直推比例
     * @param levelNo       等级序号
     * @param manageAwards  直推设置
     * @return
     */
    double countPushPercent(Integer levelNo,List<RelationAndPercent> manageAwards);
}
