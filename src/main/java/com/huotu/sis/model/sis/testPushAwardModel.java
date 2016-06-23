package com.huotu.sis.model.sis;

import com.huotu.sis.entity.support.SisRebateTeamManagerSetting;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 经营者直推奖测试model
 * Created by slt on 2016/2/19.
 */
@Getter
@Setter
public class testPushAwardModel {
    /**
     * 返利的金额
     */
    private double amount;

    /**
     * 返利的配置
     */
    private SisRebateTeamManagerSetting setting;

    /**
     * 返利的用户
     */
    private List<Integer> levelNos;

}
