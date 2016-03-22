package com.huotu.sis.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by xhk on 2016/1/8.
 * 店中店model
 */
@Getter
@Setter
public class ResultModel {


    /**
     * 返回的代码类型
     */
    private int code;

    /**
     * 返回的消息
     */
    private String message;

}
