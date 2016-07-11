package com.huotu.sis.model.sis;

import lombok.Getter;
import lombok.Setter;

/**
 * 店铺升级所需条件
 * Created by slt on 2016/1/26.
 */
@Getter
@Setter
public class SisLevelConditionsModel {
    /**
     * 等级ID
     */
    private Long levelId;
    /**
     * 等级序号
     */
    private Integer levelNo;
    /**
     * 等级名称
     */
    private String levelTitle;

    /**
     * 所需人数
     */
    private Integer num;

    /**
     * 关系（0：或，1：且，-1：无关系）
     */
    private Integer relation;

    @Override
    public String toString() {
        String rel="";
        if(relation==0){
            rel="或";
        }else if(relation==1){
            rel="且";
        }
        return rel+levelTitle+num+"人";
    }
}
