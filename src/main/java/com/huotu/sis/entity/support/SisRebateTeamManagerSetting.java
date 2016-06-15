package com.huotu.sis.entity.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 直推奖配置
 * Created by Administrator on 2016/1/25.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SisRebateTeamManagerSetting {
    /**
     * 店主获得的比例
     */
    @JsonProperty("SaleAward")
    private double saleAward;

    /**
     * 层级比例
     */
    @JsonProperty("ManageAwards")
    private List<RelationAndPercent> manageAwards;

}
