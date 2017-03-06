package com.huotu.sis.service;

import com.huotu.huobanplus.common.model.Rebate;
import com.huotu.huobanplus.common.model.RebateInfo;


/**
 * 传统金子塔返利模式计算
 * Created by allan on 2/1/16.
 */
public interface NormalRebateService {
    /**
     * 得到某个等级某个商品中得到的返利（不考虑阀值）
     *
     * @param levelId    等级id（当layerIndex为2,3时无效）
     * @param rebateInfo 返利配置,需根据是否为个性化设置不同的{@link Rebate}
     * @param layerIndex 层级:0-&gt;自己购买;1-&gt;1级上线(下线购买获得);2-&gt;2级上线;3-&gt;3级上线
     * @return 可获得的返利
     */
    double rebateAmountByLevelId(
            long levelId,
            RebateInfo rebateInfo,
            int layerIndex
    );
}
