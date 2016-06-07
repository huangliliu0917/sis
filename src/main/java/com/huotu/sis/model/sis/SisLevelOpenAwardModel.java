package com.huotu.sis.model.sis;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by slt on 2016/1/26.
 */
@Getter
@Setter
public class SisLevelOpenAwardModel {
    /**
     * id
     */
    private Long Id;

    /**
     * 上线店铺等级ID
     */
    private Long levelId;

    /**
     * 上线店铺等级名称
     */
    private String levelName;

    /**
     * 开店者店铺等级ID
     */
    private Long ownLevelId;

    /**
     * 开店者店铺等级名称
     */
    private String ownLevelName;

    /**
     * 返利余额
     */
    private Double award;
}
