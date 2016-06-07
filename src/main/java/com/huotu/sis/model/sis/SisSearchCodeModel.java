package com.huotu.sis.model.sis;

import lombok.Getter;
import lombok.Setter;

/**
 * 短信验证码查询条件
 * Created by slt on 2016/2/19.
 */
@Getter
@Setter
public class SisSearchCodeModel {
    /**
     * 手机号
     */
    private String mobile="";

    /**
     * 商户ID
     */
    private Long merchantId;

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
