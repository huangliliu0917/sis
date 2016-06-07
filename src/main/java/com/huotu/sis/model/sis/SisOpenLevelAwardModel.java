package com.huotu.sis.model.sis;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by slt on 2016/1/26.
 */
@Getter
@Setter
public class SisOpenLevelAwardModel {
    /**
     * 返利者店铺等级ID
     */
    private Long levelId;

    /**
     * 开店者店铺等级ID
     */
    private Long ownlevelId;

    /**
     * 金额
     */
    private double award;


}
