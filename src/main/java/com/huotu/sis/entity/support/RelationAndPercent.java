package com.huotu.sis.entity.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 直推奖关系和比例设置
 * Created by Administrator on 2016/1/25.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelationAndPercent {
    /**
     *  关系(如："0_1",表示：贡献人店铺等级序号为0，受益人店铺等级序号为1)
     */
    @JsonProperty("Relation")
    private String relation;

    /**
     * 比例
     */
    @JsonProperty("Percent")
    private double percent;
}
