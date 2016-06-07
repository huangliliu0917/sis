package com.huotu.sis.service;

import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.model.sis.SisSearchModel;
import org.springframework.data.domain.Page;

/**
 * Created by Administrator on 2016/1/29.
 */
public interface SisService {

    /**
     * 获取用户店铺等级ID
     * @param user
     * @return
     * @throws Exception
     */
    long getSisLevelId(User user) throws Exception;

    /**
     * 获取用户店铺的等级
     * @param user
     * @return
     * @throws Exception
     */
    Integer getSisLevel(User user) throws Exception;


    /**
     * 查找店中店的记录
     * @param sisSearchModel    查询model
     * @return
     * @throws Exception
     */
    Page<Sis> findSisList(SisSearchModel sisSearchModel) throws Exception;
}
