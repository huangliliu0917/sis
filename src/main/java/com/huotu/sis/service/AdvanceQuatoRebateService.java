package com.huotu.sis.service;

import com.huotu.huobanplus.common.model.adrebateconfig.ProductDisRebateDesc;
import com.huotu.huobanplus.common.model.adrebateconfig.RebateLayerConfig;

import java.util.List;

/**
 * 进阶版配额返利计算（8级返利模式）
 * Created by allan on 2/1/16.
 */
public interface AdvanceQuatoRebateService {
    /**
     * 某个等级能够获得的返利区间（不考虑阀值）
     *
     * @param levelId              等级id
     * @param productRebateConfigs 所有货品的返利配置信息
     * @param globalRebateConfigs  全局的返利配置信息
     * @param layerIndex           获得返利人所在的层级,0-&gt;自己,1-&gt;1级上线,2-&gt;2级上线,以此类推
     * @return 返利区间[min, max]
     */
    double[] rebateAmountIntervalByLevelId(
            long levelId,
            List<ProductDisRebateDesc> productRebateConfigs,
            List<RebateLayerConfig> globalRebateConfigs,
            int layerIndex
    );

    /**
     * 某个等级and某个货品能够返利的金额（不考虑阀值）
     *
     * @param levelId
     * @param productRebateConfig
     * @param globalRebateConfig
     * @return
     */
    double rebateAmountByLevelId(
            long levelId,
            ProductDisRebateDesc productRebateConfig,
            List<RebateLayerConfig> globalRebateConfig,
            int layerIndex
    );
}
