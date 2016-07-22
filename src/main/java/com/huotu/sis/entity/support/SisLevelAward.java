package com.huotu.sis.entity.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Administrator on 2016/7/6.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SisLevelAward {
    /**
     * 购买人开店等级
     */
    @JsonProperty("buysislvid")
    private long buySisLvId;


    @JsonProperty("cfg")
    List<OpenSisAward> cfg;
}
