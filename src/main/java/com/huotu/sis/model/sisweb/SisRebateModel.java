package com.huotu.sis.model.sisweb;

import com.huotu.huobanplus.common.entity.User;
import lombok.Getter;
import lombok.Setter;

/**
 * 返利model
 * Created by slt on 2016/1/26.
 */
@Getter
@Setter
public class SisRebateModel {
    /**
     * 返利用户
     */
    private User user;

    /**
     * 返利
     */
    private double rebate;

    /**
     * 贡献人上级的返利层级(返自己就是0，返上三级就是3)
     */
    private int layer;
}
