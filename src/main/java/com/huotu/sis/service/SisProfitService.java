package com.huotu.sis.service;

import com.huotu.sis.entity.SISProfit;

import java.util.List;

/**
 * Created by jinzj on 2016/5/9.
 */
public interface SisProfitService {

    List<SISProfit> findAllByUserLevelId(Long userLevelId, Long customerId, Long sisLevelId);
}
