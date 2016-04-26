package com.huotu.sis.service;

import com.huotu.sis.model.SisDetailModel;
import com.huotu.sis.model.SisSumAmountModel;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

/**
 * Created by jinzj on 2016/2/22.
 */
public interface SqlService {

    /**
     * 根据用户ID查找出自己团队贡献的开店奖以及人数
     * @param userId
     * @return
     * @throws IOException
     */
    List<SisSumAmountModel> getListGroupBySrcType(Long userId) throws IOException;

    /**
     * 返回会员数组
     * @param userId
     * @return
     * @throws IOException
     */
    List getListGroupByBelongType(Long userId) throws IOException;


    /**
     * 查询我的团队开店记录
     * @param userId    贡献人ID
     * @param srcType   层级
     * @param pageNo    查询的页数
     * @param pageSize  每页几条
     * @return
     * @throws IOException
     */
    Page<SisDetailModel> getListOpenShop(Long userId, Integer srcType, Integer pageNo, Integer pageSize) throws IOException;
}
