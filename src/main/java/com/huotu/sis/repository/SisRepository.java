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
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * @author CJ
 */
@RepositoryRestResource
public interface SisRepository extends JpaRepository<Sis, Long>,JpaSpecificationExecutor<Sis> {
    Sis findByUser(User user);

    @Query("select s from Sis as s where s.user in (select u from User as u where u.belongOne=?1)")
    List<Sis> findByUserWhereUserBeloneis(Long belongOneId);

//    @Query("select count(s) from Sis as s where s.user.id in(select u.id from User as u where u.belongOne=?1)")
//    Object countSisNumber(Long belongOneUserId);

    @Query("select count(s) from Sis as s inner join User as u on s.user=u where u.belongOne=?1")
    Long countSisNum(Long belongOneUserId);
//    Sis findOne(Long sisId);
//    Sis save(Sis sis);
}
