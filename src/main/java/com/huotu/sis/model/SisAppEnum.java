package com.huotu.sis.model;

import com.huotu.common.api.ICommonEnum;

/**
 * Created by Administrator on 2015/11/7.
 */
public enum SisAppEnum implements ICommonEnum {
    /**
     * SUCCESS(1, "操作成功")
     */
    SUCCESS(1, "操作成功"),

    /**
     * ERROR_APPLYING(10001, "店中店申请正在处理中，请耐心等待")
     */
    ERROR_APPLYING(10001, "店中店申请正在处理中，请耐心等待"),

    /**
     *  ERROR_PASSED(10002, "店中店申请已经通过了，没有必要再申请了")
     */
    ERROR_PASSED(10002, "店中店申请已经通过了，没有必要再申请了"),

    /**
     * ERROR_NOUSER(10003,"无此小伙伴")
     */
    ERROR_NOUSER(10003,"无此小伙伴"),

    /**
     * ERROR_NOSIS(10005,"无此店铺")
     */
    ERROR_NOSIS(10005,"无此店铺"),

    /**
     * ERROR_PARAMETER_EXCEPTION(10004,"参数异常")
     */
    ERROR_PARAMETER_EXCEPTION(10004,"参数异常"),


    /**
     *  ERROR_MOBILE(20001, "手机号格式不正确")
     */
    ERROR_MOBILE(20001, "手机号格式不正确"),

    /**
     * ERROR_REPEATGOODS(30001,"已经添加过该商品了")
     */
    ERROR_REPEATSISGOODS(30001,"已经添加过该商品了"),

    /**
     * ERROR_NOGOODS(30002,"无此商品")
     */
    ERROR_NOGOODS(30002,"无此商品"),

    /**
     * ERROR_UP(30003,"上架错误")
     */
    ERROR_GOODS_UP(30003,"上架错误");




   private int value;

   private String name;

    SisAppEnum(int value, String name) {
        this.value=value;
        this.name=name;
    }


    @Override
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
