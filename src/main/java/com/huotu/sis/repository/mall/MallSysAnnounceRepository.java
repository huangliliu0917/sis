/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.MallSysAnnounce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by helloztt on 2016/10/14.
 * @since 1.6.0
 */
@Repository
public interface MallSysAnnounceRepository extends JpaRepository<MallSysAnnounce, Long>{

    List<MallSysAnnounce> findTop4ByIsCheckedTrueOrderByIsTopDescAddTimeDesc();
}
