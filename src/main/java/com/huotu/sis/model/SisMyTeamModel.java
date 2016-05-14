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
     * 姓名
     */
    private String name;

    /**
     * 店铺店铺等级名称
     */
    private String partnerLevelName;
    /**
     * 上级姓名
     */
    private String belongOneName;
    /**
     * 团队名称
     */
    private String teamName;

    /**
     * 类型 1总代1 2总代2
     */
    private Long type;
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
