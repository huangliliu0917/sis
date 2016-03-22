package com.huotu.sis.model;

import lombok.Getter;
import lombok.Setter;
import net.sourceforge.jtds.jdbc.DateTime;

/**
 * Created by admin on 2016/1/31.
 */
@Setter
@Getter
public class UserTempIntegralHistoryModel
{
     private int utih_id;

    private int utih_customerId;

    private int utih_userId;

    private int utih_type;

    private int utih_integral;

    private int utih_status;

    private DateTime utih_addTime;

    private DateTime utih_updateTime;

    private String utih_desc;

    private int utih_contribute_userID;

    private String utih_order_id;

    private int utih_flowIntegral;

    private int utih_positiveFlag;

    private DateTime utih_estimate_postime;

    private DateTime utih_positiveTime;

    private int utih_userLevelId;

    private int utih_userGroupId;

    private int utih_userType;

    private int utih_newType;

    private int utih_contribute_userType;

    private  int utih_contriibute_belongone;

    private String utih_contribute_desc;

    private String union_order_id;

    private int product_id;
}
