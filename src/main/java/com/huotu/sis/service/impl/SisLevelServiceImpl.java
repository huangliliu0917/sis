package com.huotu.sis.service.impl;

import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.repository.GoodsRepository;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.entity.support.OpenGoodsIdLevelId;
import com.huotu.sis.entity.support.OpenGoodsIdLevelIds;
import com.huotu.sis.repository.SisConfigRepository;
import com.huotu.sis.repository.SisLevelRepository;
import com.huotu.sis.repository.SisRepository;
import com.huotu.sis.service.SisLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2016/1/29.
 */
@Service
public class SisLevelServiceImpl implements SisLevelService {
    @Autowired
    SisConfigRepository sisConfigRepository;

    @Autowired
    SisRepository sisRepository;

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    SisLevelRepository sisLevelRepository;

    @Override
    public SisLevel getUpgradeSisLevel(Integer numbers,User user) throws Exception {
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(user.getMerchant().getId());
        if(sisConfig==null||sisConfig.getEnabled()==0){
            return null;
        }
        if(sisConfig.getOpenGoodsMode()==1){
            Sis sis=sisRepository.findByUser(user);
            //获取当前等级的开店商品
            Goods sisLevelGoods=getSisShopOpenGoods(user,sisConfig);
            if(sisLevelGoods==null){
                return null;
            }
            OpenGoodsIdLevelIds openGoodsIdLevelIds=sisConfig.getOpenGoodsIdlist();
            Double sisLevelPrice=sisLevelGoods.getPrice();
            for(OpenGoodsIdLevelId o:openGoodsIdLevelIds.values()){
                SisLevel sisLevel=sisLevelRepository.findOne(o.getLevelid());
                if(sisLevel.getLevelNo()<=sis.getSisLevel().getLevelNo()){
                    continue;
                }
                Goods goods=goodsRepository.findOne(o.getGoodsid());
                Double differencePrice=Math.floor(goods.getPrice()-sisLevelPrice);
                if(Integer.valueOf(differencePrice.byteValue()).equals(numbers)){
                    return sisLevel;
                }

            }
        }
        return null;
    }

    @Override
    public Goods getSisShopOpenGoods(User user,SisConfig sisConfig) throws Exception {
        Sis sis=sisRepository.findByUser(user);
        SisLevel sisLevel=sis.getSisLevel();
        if(sisLevel!=null){
            for(OpenGoodsIdLevelId o:sisConfig.getOpenGoodsIdlist().values()){
                if(sisLevel.getId().equals(o.getLevelid())){
                    Goods goods=goodsRepository.findOne(o.getGoodsid());
                    return goods;
                }

            }

        }
        return null;
    }
}
