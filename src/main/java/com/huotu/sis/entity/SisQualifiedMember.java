/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.Description;

import javax.persistence.*;

/**
 *
 * <p>用户店中店开店资格表</p>
 *
 * @author shiliting
 */
@Entity
@Table(name = "SIS_QualifiedMember")
@Getter
@Setter
@Cacheable(value = false)
public class SisQualifiedMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    @Description("ID")
    private Long id;

    @Column(name = "MemberId")
    @Description("会员ID")
    private Long memberId;

    @Column(name = "CustomerId")
    @Description("商户ID")
    private Long customerId;


}
