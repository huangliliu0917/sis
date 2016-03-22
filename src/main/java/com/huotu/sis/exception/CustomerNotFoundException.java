package com.huotu.sis.exception;

/**
 * 商户未找到
 * Created by slt on 2016/1/12.
 */
public class CustomerNotFoundException extends Exception {

    public CustomerNotFoundException(String message){
        super(message);
    }
}
