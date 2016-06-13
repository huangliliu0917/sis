package com.huotu.sis.service;

import com.huotu.huobanplus.common.entity.Order;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.entity.UserTempIntegralHistory;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.model.sis.SisSearchModel;
import org.springframework.data.domain.Page;

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
    void calculatePushAward(User user,Order order,String unionOrderId) throws Exception;

    /**
     * 经营者模式计算直推奖
     * @throws Exception
     */
    void countProprietor() throws Exception;

    /**
     * 默认直推奖计算
     * @throws Exception
     */
    void countDefPush() throws Exception;

    /**
     * 保存临时积分表
     * @return
     * @throws Exception
     */
    UserTempIntegralHistory saveUserTempIntegralHistory() throws Exception;

}
