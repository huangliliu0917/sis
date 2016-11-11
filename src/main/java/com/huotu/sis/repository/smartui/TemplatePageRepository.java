/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.repository.smartui;

import com.huotu.huobanplus.smartui.entity.TemplatePage;
import com.huotu.huobanplus.smartui.entity.support.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @since 1.2
 */
@RepositoryRestResource
public interface TemplatePageRepository extends JpaRepository<TemplatePage, Long>, JpaSpecificationExecutor<TemplatePage> {

    //Spring BUG
    @Override
    List<TemplatePage> findAll();

    Page<TemplatePage> findByScopeAndEnabled(@RequestParam("scope")
                                             @Param("scope")
                                             Scope scope,
                                             @RequestParam("enabled")
                                             @Param("enabled")
                                             boolean enabled,
                                             Pageable pageable);

    /**
     * 参考<a href="http://hottech.gitcafe.io/resources/huobanplusdocs/SmartUI%E8%A7%86%E5%9B%BE%E6%8E%A7%E4%BB%B6%E6%9C%8D%E5%8A%A1.pdf">
     * 文档</a>templatePages章节
     *
     * @param scope      范畴 类型枚举 包括global,sis,system，必选
     * @param enabled    启用状态，必选
     * @param merchantId 商户号，必选
     * @param pageable   分页
     * @return
     * @since 1.4
     */
    Page<TemplatePage> findByScopeAndEnabledAndMerchantId(@RequestParam("scope")
                                                          @Param("scope")
                                                          Scope scope,
                                                          @RequestParam("enabled")
                                                          @Param("enabled")
                                                          boolean enabled,
                                                          @RequestParam("merchantId")
                                                          @Param("merchantId")
                                                          Long merchantId,
                                                          Pageable pageable);

    @RestResource(exported = false)
    List<TemplatePage> findByScopeAndEnabled(@RequestParam("scope")
                                             @Param("scope")
                                             Scope scope,
                                             @RequestParam("enabled")
                                             @Param("enabled")
                                             boolean enabled);
}
