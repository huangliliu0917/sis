package com.huotu.sis.service;

import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisInviteLog;
import com.huotu.sis.entity.SisLevel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by lgh on 2015/12/30.
 */
public interface UserService {

    /**
     * 获取用户Id
     * 从加密的cookie中取出数据
     *
     * @param request
     * @return
     */
    Long getUserId(HttpServletRequest request) throws Exception;

    /**
     * 对用户的id进行加密
     * 放入cookie中
     *
     */
    void setUserId(Long userId, HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 将登录类型type保存到cookie中去
     * @param loginType     0：微信，1 app
     * @param request       请求
     * @param response      响应
     * @throws Exception
     */
    void setLoginType(HttpServletRequest request, HttpServletResponse response, String loginType) throws Exception;

    /**
     * 获取登录类型
     * @param request
     * @return
     * @throws Exception
     */
    String getLoginType(HttpServletRequest request) throws Exception;

    /**
     * 开店
     *
     * @param userId
     */
    void open(Long userId);

    /**
     * 新的开店逻辑
     *      1.增加一条店中店记录
     *      2.开店奖计算
     *      3.合伙人送股份
     * @param user
     * @throws Exception
     */
    void newOpen(User user, String orderId, SisConfig sisConfig) throws Exception;

    /**
     * 用户开店之后根据商户配置给用户的上级返利
     * @param user          开店的用户
     * @throws Exception
     */
    void countOpenShopAward(User user, String orderId, String unionOrderId,SisConfig sisConfig) throws Exception;


    /**
     * 给某个用户开店奖返利
     * @param earningsUser          返利的用户
     * @param contributeUser        贡献人
     * @param money                 返利的钱
     * @throws Exception
     */
    void rebateOpenShop(User earningsUser, User contributeUser, Double money, String orderId, String unionOrderId, Integer srcType) throws Exception;


    /**
     * 等级初始化,查找商家是否配置了店中店等级，如果没有配置则默认插入一条一级的等级(弃用)
     * @param customerId 商家ID
     * @return
     * @throws Exception
     */
    SisLevel initSisLevel(Long customerId) throws Exception;


    /**
     * 合伙人送股
     * @param user
     * @param orderId
     * @throws Exception
     */
    void givePartnerStock(User user, String orderId,SisConfig sisConfig) throws Exception;

    /**
     * 查找用户最近填写的开店邀请记录
     * @param acceptId      接受邀请人的ID
     * @return
     * @throws Exception
     */
    SisInviteLog findUserLatestInviteLog(Long acceptId) throws Exception;

    /**
     * 根据用户ID得到该用户产生关系（上级，上上级等）的一条线
     * @param userId
     * @return
     * @since 2.0
     */
    List<User> getAllRelationByUserId(Long userId);

}
