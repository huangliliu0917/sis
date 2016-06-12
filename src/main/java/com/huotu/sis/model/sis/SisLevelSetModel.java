package com.huotu.sis.model.sis;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lgh on 2016/3/31.
 */
@Getter
@Setter
public class SisLevelSetModel {
    /**
     * 商户ID
     */
    private long customerId;

    /**
     * 直推比例
     */
    private double rebate;

    /**
     * 等级ID
     */
    private int id;

    /**
     * 等级序号
     */
    private int level;

    /**
     * 直推店铺数量
     */
    private Integer zhituidianpuAmount;

    /**
     * 团队店铺数量
     */
    private Integer tuanduidianpuAmount;

    /**
     * 等级名称
     */
    private String nickname;

    /**
     * 是否为默认等级
     */
    private Integer isSystem;

    /**
     * 是否启用补差价 0：不启用，1：启用
     */
    private Integer extraUpgrade;
}
