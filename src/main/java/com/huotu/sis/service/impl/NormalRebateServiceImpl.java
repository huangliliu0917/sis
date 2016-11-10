package com.huotu.sis.service.impl;

import com.huotu.huobanplus.common.model.RebateInfo;
import com.huotu.huobanplus.common.model.RebatePair;
import com.huotu.sis.service.NormalRebateService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by allan on 2/2/16.
 */
@Service
public class NormalRebateServiceImpl implements NormalRebateService {
    @Override
    public double rebateAmountByLevelId(long levelId, RebateInfo rebateInfo, int layerIndex) {
        List<RebatePair> rebatePairs = null;
        switch (layerIndex) {
            case 0:
                //自己购买获得
                rebatePairs = rebateInfo.getRebate().getSelfRatios();
                break;
            case 1:
                rebatePairs = rebateInfo.getRebate().getBelong1Ratios();
                break;
            case 2:
                rebatePairs = rebateInfo.getRebate().getBelong2Ratios();
                break;
            case 3:
                rebatePairs = rebateInfo.getRebate().getBelong3Ratios();
                break;
        }
        if (rebatePairs != null) {
            int ratio = getRebateRatio(levelId, rebatePairs, layerIndex);
            return (double) ratio / 100 * (rebateInfo.getAmount() * rebateInfo.getRatio());
        }
        return 0;
    }

    /**
     * 得到返利百分比
     *
     * @param levelId
     * @param rebatePairs
     * @param layerIndex
     * @return
     */
    private int getRebateRatio(long levelId, List<RebatePair> rebatePairs, int layerIndex) {
        if (layerIndex > 1) {
            levelId = -1;
        }
        final long finalLevelId = levelId;
        RebatePair rebatePair = rebatePairs.stream().filter(rp -> rp.getLevelId() == finalLevelId).findFirst().
                orElse(new RebatePair());
        return rebatePair.getRatio();
    }
}
