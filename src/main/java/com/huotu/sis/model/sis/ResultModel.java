package com.huotu.sis.model.sis;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lgh on 2016/3/31.
 */
@Getter
@Setter
public class ResultModel {
    /**
     * Created by Administrator on 2016/1/26.
     */


    private int error;

    private int code;

    private String message;

    private String url;

    private Object data;

}
