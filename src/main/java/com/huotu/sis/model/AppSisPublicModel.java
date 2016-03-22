package com.huotu.sis.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lgh on 2015/11/12.
 */
@Getter
@Setter
public class AppSisPublicModel {
    /**
     * 签名
     */
    private String sign;
    /**
     * 时间戳
     */
    private long timestamp;
    /**
     * appID
     */
    private String appid;
    /**
     * 版本
     */
    private String version;

    /**
     *  操作
     */
    private String operation;
}
