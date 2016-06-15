package com.huotu.sis.service.impl;

import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.OrderItems;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.repository.GoodsRepository;
import com.huotu.huobanplus.common.repository.UserRepository;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.entity.support.OpenGoodsIdLevelId;
import com.huotu.sis.model.sisweb.OpenLevelGoodsModel;
import com.huotu.sis.repository.SisConfigRepository;
import com.huotu.sis.repository.SisLevelRepository;
import com.huotu.sis.repository.SisRepository;
import com.huotu.sis.service.SisLevelService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * Created by jinzj on 2016/4/6.
 */
@Service
public class SisLevelServiceImpl implements SisLevelService {
    private static Log log = LogFactory.getLog(SisLevelServiceImpl.class);

    @Autowired
    SisConfigRepository sisConfigRepository;

    @Autowired
    SisRepository sisRepository;

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    private SisLevelRepository sisLevelRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<SisLevel> getListBelongByLevelId(Integer levelNo, Long customerId) {
        Sort sort = new Sort(Sort.Direction.ASC, "levelNo");
        return sisLevelRepository.findAll(((root, query, cb) -> {
            Predicate predicate = cb.and(
                    cb.equal(root.get("merchantId").as(Long.class), customerId),
                    cb.gt(root.get("levelNo").as(Long.class), levelNo),
                    cb.equal(root.get("extraUpgrade").as(Integer.class), 1)
            );
            return predicate;
        }), sort);
    }


    @Override
    public SisLevel getUpgradeSisLevel(SisConfig sisConfig,Sis sis,OrderItems orderItems,User user) throws Exception {
        //获取店中店配置的开店等级对应的商品list
        List<OpenLevelGoodsModel>openLevelGoodsModels=getSisOpenGoods(sisConfig);
        //获取当前等级的开店商品
        Goods sisLevelGoods=null;
        for(OpenLevelGoodsModel o:openLevelGoodsModels){
            if(o.getSisLevel().getId().equals(sis.getSisLevel().getId())){
                sisLevelGoods=o.getGoods();
            }
        }
        if(sisLevelGoods==null){
            //店中店等级找不到对应商品
            log.info("user"+user.getId()+"The inn in inn level can't find the corresponding goods");
            return null;
        }
        //获取补差价+当前等级的开店商品
        for(int i=openLevelGoodsModels.size()-1;i>=0;i--){
            Double levelGoodsprice=sisLevelGoods.getPrice()+orderItems.getAmount()*orderItems.getPrice();
            OpenLevelGoodsModel openLevelGoodsModel=openLevelGoodsModels.get(i);
            if(levelGoodsprice>=openLevelGoodsModel.getGoods().getPrice()
                    &&openLevelGoodsModel.getSisLevel().getExtraUpgrade()!=null&&
                    openLevelGoodsModel.getSisLevel().getExtraUpgrade()==1){
                return openLevelGoodsModels.get(i).getSisLevel();
            }

        }
        return null;
    }

    @Override
    public SisLevel getSisLevelByOfflineSisNum(User user) throws Exception {
        SisLevel newSisLevle=new SisLevel();
        Long sisNumber=sisRepository.countSisNum(user.getId());
        if(sisNumber==null){
            return newSisLevle;
        }
        List<SisLevel> sisLevels=sisLevelRepository.findAll();

        for(SisLevel l:sisLevels){
            if(l.getUpShopNum()==null){
                continue;
            }
            if(sisNumber>=l.getUpShopNum()){
                newSisLevle=l;
            }
        }
        return newSisLevle;
    }


    @Override
    public boolean upgradeSisLevel(User user, SisConfig sisConfig, OrderItems orderItems) throws Exception {
        Sis sis=sisRepository.findByUser(user);
        //获取应该升级到的等级
        SisLevel sisLevel=getUpgradeSisLevel(sisConfig,sis,orderItems,user);

        if(sisLevel==null){
            return false;
        }
        log.debug("userID"+user.getId()+"can update level to "+sisLevel.getId());
        //把该等级保存到用户店中店表中
        saveSisLevel(sis,sisLevel);

        return true;
    }

    @Override
    public void upgradeSisLevelByUpShopNum(User user) throws Exception {
        User beloneOneUser=userRepository.findOne(user.getBelongOne());
        if(beloneOneUser==null){
            log.info("user:"+user.getId()+" have no beloneOneUser");
            return;
        }
        Sis sis=sisRepository.findByUser(beloneOneUser);
        if(sis==null){
            log.info("user:"+beloneOneUser.getId()+" Sis is null");
            return;
        }
        SisLevel sisLevel= getSisLevelByOfflineSisNum(beloneOneUser);
        if(sisLevel==null){
            log.info("user:"+beloneOneUser.getId()+" Unable to get the store level");
            return;
        }
        saveSisLevel(sis,sisLevel);

    }

    @Override
    public void saveSisLevel(Sis sis, SisLevel sisLevel) throws Exception {
        if(sisLevel.getLevelNo()==null||sis.getSisLevel()==null||sis.getSisLevel().getLevelNo()==null){
            return;
        }
        //保证跟新的等级大于当前的等级
        if(sis.getSisLevel().getLevelNo()>=sisLevel.getLevelNo()){
            log.info("user"+sis.getUser().getId()+"Upgrade level is lower than the current level");
            return;
        }
        sis.setSisLevel(sisLevel);
        sisRepository.save(sis);
    }



    @Override
    public List<OpenLevelGoodsModel> getSisOpenGoods(SisConfig sisConfig) throws Exception {
        List<OpenLevelGoodsModel>openLevelGoodsModels=new ArrayList<>();
        List<Long> levelIds=new ArrayList<>();
        List<Long> goodsIds=new ArrayList<>();
        for(OpenGoodsIdLevelId o:sisConfig.getOpenGoodsIdlist().values()){
            levelIds.add(o.getLevelid());
            goodsIds.add(o.getGoodsid());
        }
        List<SisLevel> levelList=sisLevelRepository.findByIdIn(levelIds);
        List<Goods> goodsList=goodsRepository.findByIdIn(goodsIds,sisConfig.getMerchantId());
        for(OpenGoodsIdLevelId o:sisConfig.getOpenGoodsIdlist().values()){
            OpenLevelGoodsModel openLevelGoodsModel=new OpenLevelGoodsModel();
            SisLevel level=levelList.stream().filter(sisLevel -> o.getLevelid().equals(sisLevel.getId())).findFirst().get();
            Goods sisGoods=goodsList.stream().filter(goods->o.getGoodsid().equals(goods.getId())).findFirst().get();
            openLevelGoodsModel.setSisLevel(level);
            openLevelGoodsModel.setGoods(sisGoods);
            openLevelGoodsModels.add(openLevelGoodsModel);
        }
        //排序从等级低到等级高
        Collections.sort(openLevelGoodsModels, new Comparator<OpenLevelGoodsModel>() {
            @Override
            public int compare(OpenLevelGoodsModel o1, OpenLevelGoodsModel o2) {
                return o1.getSisLevel().getLevelNo()-o2.getSisLevel().getLevelNo();
            }
        });

        return openLevelGoodsModels;
    }
}
