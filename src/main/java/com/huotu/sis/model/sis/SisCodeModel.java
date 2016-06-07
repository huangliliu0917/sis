package com.huotu.sis.model.sis;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 短信model
 * Created by slt on 2016/2/19.
 */
@Getter
@Setter
public class SisCodeModel {
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 验证码
     */
    private String code;
    /**
     * 发送时间
     */
    private Date sendTime;

}
