package com.huotu.sis.model.sisweb;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jinzj on 2016/1/25.
 */
@Getter
@Setter
public class SisBrandModel {

    private Long brandId;
    private String brandName;
    private String brandLogo;
    private Long customerId;
    private boolean selected;
    private String detailsUrl;
    /**
     * 是否是一键铺货模式
     */
    private boolean shelves;
}
