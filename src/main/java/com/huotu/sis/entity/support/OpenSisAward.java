package com.huotu.sis.entity.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Administrator on 2016/1/25.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenSisAward {
    /**
     * 层级
     */
    @JsonProperty("idx")
    private long idx;

    /**
     * 是否个性化，-1为个性化，否则就是佣金
     */
    @JsonProperty("unified")
    private double unified;

    /**
     * 个性化
     */
    @JsonProperty("custom")
    private List<LevelIdAndVal> custom;

}
