package com.huotu.sis.exception;

/**
 * 用户未找到
 * Created by slt on 2016/1/12.
 */
public class UserNotFoundException extends Exception {

    public UserNotFoundException(String message){
        super(message);
    }
}
