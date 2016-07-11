package com.huotu.sis.entity.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 等级升级条件
 * Created by Administrator on 2016/7/6.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SisLevelCondition {
    /**
     * 店铺等级ID
     */
    @JsonProperty("sislvid")
    private long sisLvId;

    /**
     * 所需人数
     */
    @JsonProperty("num")
    private int number;

    /**
     * 关系（0：或，1：且，-1：无关系）
     */
    @JsonProperty("relation")
    private int relation;
}
