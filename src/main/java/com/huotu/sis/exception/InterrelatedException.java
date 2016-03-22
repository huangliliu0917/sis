/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.exception;

/**
 * 第三方错误
 * <p>该错误发生无需跟用户汇报，但需要跟运营人士汇报</p>
 *
 * @author CJ
 */
public class InterrelatedException extends Exception {
    public InterrelatedException(Throwable cause) {
        super(cause);
    }

    public InterrelatedException() {
    }

    public InterrelatedException(String message) {
        super(message);
    }

    public InterrelatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterrelatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
