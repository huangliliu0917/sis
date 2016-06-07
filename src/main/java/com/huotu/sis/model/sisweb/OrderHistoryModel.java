package com.huotu.sis.model.sisweb;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jinzj on 2016/2/2.
 *
 * @since 1.3
 */
@Getter
@Setter
public class OrderHistoryModel {

    private String orderId;

    private String orderName;

    //订单状态
    private int orderStatus;

    private String addDate;

    private int integral;

    private String loginName;

    private String mobile;

    private Double price;

}
