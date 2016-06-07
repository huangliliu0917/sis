package com.huotu.sis.model.sis;

import lombok.Getter;
import lombok.Setter;

/**
 * 后台商品查询条件
 * Created by slt on 2015/12/9.
 */
@Getter
@Setter
public class GoodSearchModel {
    /**
     * 商品名
     */
    private String name="";

    /**
     * 分类路径
     */
    private String catPath="";

    /**
     * 指定查询页码
     */
    private Integer pageNoStr = 0;

}
