package com.huotu.sis.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lgh on 2016/5/9.
 */

@Getter
@Setter
public class SisMyTeamModel {
    /**
     * 直推人数
     */
    private Long directNumber;
    /**
     * 间推人数
     */
    private Long indirectNumber;

    /**
     * 新增人数(直推+间推)
     */
    private Long todayNumber;
}
