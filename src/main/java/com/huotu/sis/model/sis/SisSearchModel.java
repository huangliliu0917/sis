package com.huotu.sis.model.sis;

import lombok.Getter;
import lombok.Setter;

/**
 * 店中店查询条件
 * Created by slt on 2016/2/19.
 */
@Getter
@Setter
public class SisSearchModel {
    /**
     * 开店用户登录名
     */
    private String userLoginName="";

    /**
     * 商家ID
     */
    private Long customerId;

    /**
     * 发布时间
     */
    private String startTime="";
    /**
     * 结束时间
     */
    private String endTime="";
    /**
     * 指定查询页码
     */
    private Integer pageNoStr = 0;

}
