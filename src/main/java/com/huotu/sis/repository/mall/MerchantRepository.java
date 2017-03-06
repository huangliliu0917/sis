/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商户服务
 * <p>
 * 响应编码
 * http://docs.spring.io/spring-data/rest/docs/2.3.2.RELEASE/reference/html/#repository-resources.default-status-codes
 * 200 成功
 * 201 Created
 * 204 No Content
 * <p>
 * 集合动作 指的是直接访问资源 比如 http://api.huobanplus.com/merchants
 * http://docs.spring.io/spring-data/rest/docs/2.3.2.RELEASE/reference/html/#_supported_http_methods
 * GET
 * 获取资源 支持参数 page size sort
 * <p>
 * POST
 * 新建资源
 * <p>
 * 单资源动作  比如 http://api.huobanplus.com/merchants/1
 * GET
 * 获取资源
 * PUT
 * 更新资源
 * PATCH
 * 跟PUT类似，但它是一个部分更新动作
 * DELETE
 * 删除资源
 * 这里开始商户特有动作
 *
 * @author CJ
 */
@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {

    /**
     * 可管理商户的角色
     */
    String ManagerRole = "MERCHANT_MANAGER";
    /**
     * 可登录商户的角色
     */
    String AuthentificationRole = "MERCHANT_AUTHENTIFICATION";


    @Query("select m.nickName from Merchant as m where m.id=?1")
    String findNickNameByMerchantId(Long merchantId);

    @Query("select m.subDomain from Merchant as m where m.id=?1")
    String findSubDomainByMerchantId(Long merchantId);

    @Override
    void delete(Merchant entity);

    @Override
    void delete(Long aLong);

    @Override
    <S extends Merchant> S save(S entity);

    @Override
    Merchant findOne(Long aLong);

    @Override
    void delete(Iterable<? extends Merchant> entities);

    @Override
    List<Merchant> findAll();

    @Override
    List<Merchant> findAll(Iterable<Long> longs);

    @Override
    List<Merchant> findAll(Sort sort);

    @Override
    Page<Merchant> findAll(Pageable pageable);

    /**
     * 商户认证服务，具有商户认证权限的平台用户都可以认证商户。
     *
     * @param username 商户的登录名
     * @param password 商户的密码(经过一次md5以后的小写Hex)
     * @return 商户实体
     */
    Merchant findByLoginNameAndLoginPassword(@Param("username") String username, @Param("password") String password);
}
