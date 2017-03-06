package com.huotu.sis.service.impl;

import com.huotu.huobanplus.common.model.adrebateconfig.ProductDisRebateDesc;
import com.huotu.huobanplus.common.model.adrebateconfig.RebateLayerConfig;
import com.huotu.huobanplus.common.model.adrebateconfig.RebateLevelConfig;
import com.huotu.sis.service.AdvanceQuatoRebateService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created by allan on 2/1/16.
 */
@Service
public class AdvanceQuatoRebateServiceImpl implements AdvanceQuatoRebateService {
    @Override
    public double[] rebateAmountIntervalByLevelId(long levelId, List<ProductDisRebateDesc> productRebateConfigs, List<RebateLayerConfig> globalRebateConfigs, int layerIndex) {
        double min = 0, max = 0;
        int index = 0;
        for (ProductDisRebateDesc productRebateConfig : productRebateConfigs) {
            double temp = rebateAmountByLevelId(levelId, productRebateConfig, globalRebateConfigs, layerIndex);

            if (index == 0) {
                min = temp;
                max = temp;
            }

            min = Math.min(min, temp);
            max = Math.max(max, temp);
            index++;
        }
        return new double[]{min, max};
    }

    @Override
    public double rebateAmountByLevelId(long levelId, ProductDisRebateDesc productRebateConfig, List<RebateLayerConfig> globalRebateConfig, int layerIndex) {
        double amount = 0;
        if(StringUtils.isEmpty(globalRebateConfig)||globalRebateConfig.size()==0)
            return amount;
        if (productRebateConfig.getIsCustom() == 0) {
            //使用全局,统一计算
            RebateLayerConfig currentLayerConfig = globalRebateConfig.stream()
                    .filter(config -> config.getIndex() == layerIndex)
                    .findFirst().get();
            double curPercent;

            if (currentLayerConfig.getUnifiedVal() >= 0) {
                //有统一设置的百分比
                curPercent = currentLayerConfig.getUnifiedVal();
                amount = productRebateConfig.getAmount() * curPercent / 100;
            } else {
                //从等级上设置的百分比
                RebateLevelConfig levelConfig = currentLayerConfig.getCustomVals().stream()
                        .filter(lc -> lc.getLevelId() == levelId)
                        .findFirst().get();
                curPercent = levelConfig.getValue();
                amount = productRebateConfig.getAmount() * curPercent / 100;
            }

        } else {
            //使用个性化计算,个性化存的是直接金额
            List<RebateLayerConfig> proLayerConfigs = productRebateConfig.getCustomConfig();
            RebateLayerConfig currentLayerConfig = proLayerConfigs.stream()
                    .filter(config -> config.getIndex() == layerIndex)
                    .findFirst().get();
            if (currentLayerConfig.getUnifiedVal() >= 0) {
                amount = currentLayerConfig.getUnifiedVal();
            } else {
                RebateLevelConfig levelConfig = currentLayerConfig.getCustomVals().stream()
                        .filter(lc -> lc.getLevelId() == levelId)
                        .findFirst().get();
                amount = levelConfig.getValue();
            }
        }
        return amount;
    }
}
