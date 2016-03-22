package com.huotu.sis.entity.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Administrator on 2016/1/25.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelIdAndVal {
    /**
     * 店铺等级
     */
    @JsonProperty("lvid")
    private long lvid;

    /**
     * 佣金
     */
    @JsonProperty("val")
    private double val;
}
