package com.huotu.sis.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jinzj on 2016/2/22.
 */
@Getter
@Setter
public class SisSumAmountModel {
    /**
     * 层级名字
     */
    private String typeName;
    /**
     * 下级会员的层级
     */
    private Integer srcType;
    /**
     * 每个层级贡献的总金额
     */
    private Double amount;
    /**
     * 每个层级贡献的总人数
     */
    private Integer userNum;
}
