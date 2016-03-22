/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.repository;

import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.Sis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * @author CJ
 */
@RepositoryRestResource
public interface SisRepository extends JpaRepository<Sis, Long> {
    Sis findByUser(User user);
//    Sis findOne(Long sisId);
//    Sis save(Sis sis);
}
