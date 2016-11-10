/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.Description;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;


/**
 * @author CJ
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("select u.wxNickName from User as u where u.id=?1")
    String findWxNickNameById(Long id);

    /**
     * 根据商户号和微信唯一编码获取用户列表
     *
     * @param merchantId 商户号
     * @param unionId    微信唯一编码
     * @return 用户列表
     * @since 1.3
     */
    @Description("...")
    List<User> findByMerchant_IdAndBinding_UnionId(
            @Param("merchantId")
            Long merchantId,
            @Param("unionId")
            String unionId
    );

    /**
     * 按照手机号码搜索,暂不公布.
     *
     * @param mobile 输入的手机号码
     * @return 手机号码于此一致的用户List
     * @since 1.3
     */
    List<User> findByMobile(String mobile);

    /**
     * 统计直接下线数
     *
     * @param belongOne 上线id
     * @return 直接下线总数
     * @since 1.5.0
     */
    Long countByBelongOne(Long belongOne);

    // 删除
    @Override
    void delete(User entity);

    @Override
    void deleteInBatch(Iterable<User> entities);

    @Override
    void deleteAllInBatch();

    //保存
    @Override
    <S extends User> List<S> save(Iterable<S> entities);

    @Override
    <S extends User> S save(S entity);

    @Description("根据用户数组id得到用户数组")
    @Query("select user from User user where user.id in :ids")
    List<User> findByUserIds(
            @Param("ids")
            Collection<Long> ids);

    @Description("修改用户的积分")
    @Modifying
    @Query("update User as user set user.userTempIntegral=user.userTempIntegral+?1 where user.id=?2")
    @Transactional
    int updateUserTempIntegral(Integer userAddTempIntegral, Long userId);

}
