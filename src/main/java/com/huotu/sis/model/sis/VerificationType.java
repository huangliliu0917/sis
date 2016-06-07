package com.huotu.sis.model.sis;

import com.huotu.common.api.ICommonEnum;

/**
 * Created by Administrator on 2016/1/31.
 */
public enum VerificationType implements ICommonEnum {
    openBySelf(0, "开店"),

    openByInvite(1,"邀请开店");

    private int value;
    private String name;

    VerificationType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
