package com.huotu.sis.model;

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
}
