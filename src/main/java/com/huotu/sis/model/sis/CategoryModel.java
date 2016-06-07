package com.huotu.sis.model.sis;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Administrator on 2015/12/23.
 */
@Getter
@Setter
public class CategoryModel {
    /**
     * ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String title;
    /**
     * 路径
     */
    private String catPath;

    /**
     * 路径深度
     */
    private Integer depth;
}
