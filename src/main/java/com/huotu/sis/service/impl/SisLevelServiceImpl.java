package com.huotu.sis.service.impl;

import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.OrderItems;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.entity.support.OpenGoodsIdLevelId;
import com.huotu.sis.entity.support.OpenSisAward;
import com.huotu.sis.entity.support.SisLevelAward;
import com.huotu.sis.entity.support.SisLevelCondition;
import com.huotu.sis.model.sis.SimpleSisLevelModel;
import com.huotu.sis.model.sis.SisLevelConditionsModel;
import com.huotu.sis.model.sisweb.OpenLevelGoodsModel;
import com.huotu.sis.model.sisweb.SisLevelModel;
import com.huotu.sis.repository.SisConfigRepository;
import com.huotu.sis.repository.SisLevelRepository;
import com.huotu.sis.repository.SisRepository;
import com.huotu.sis.repository.mall.GoodsRepository;
import com.huotu.sis.repository.mall.UserRepository;
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
    public List<SimpleSisLevelModel> getSimpleSisLevelModel(Long customerId) {
        List<SisLevel> sisLevels=sisLevelRepository.findByMerchantIdOrderByLevelNoAsc(customerId);
        List<SimpleSisLevelModel> models=new ArrayList<>();
        sisLevels.forEach(level -> {
            SimpleSisLevelModel sisLevelModel=new SimpleSisLevelModel();
            sisLevelModel.setLevelId(level.getId());
            sisLevelModel.setLevelNo(level.getLevelNo());
            sisLevelModel.setLevelTitle(level.getLevelName());
            models.add(sisLevelModel);
        });
        SimpleSisLevelModel sisLevelModel=new SimpleSisLevelModel();
        sisLevelModel.setLevelId(0L);
        sisLevelModel.setLevelNo(0);
        sisLevelModel.setLevelTitle("无等级限制店铺");
        models.add(sisLevelModel);
        return models;
    }

    @Override
    public List<SimpleSisLevelModel> getSimpleSisLevelModel(Long customerId, Integer LTlevelNo) {
        List<SisLevel> sisLevels=sisLevelRepository.findByMerchantIdOrderByLevelNoAsc(customerId);
        List<SimpleSisLevelModel> models=new ArrayList<>();
        sisLevels.forEach(level -> {
            if(LTlevelNo==null||level.getLevelNo()<LTlevelNo){
                SimpleSisLevelModel sisLevelModel=new SimpleSisLevelModel();
                sisLevelModel.setLevelId(level.getId());
                sisLevelModel.setLevelNo(level.getLevelNo());
                sisLevelModel.setLevelTitle(level.getLevelName());
                models.add(sisLevelModel);
            }
        });
        SimpleSisLevelModel sisLevelModel=new SimpleSisLevelModel();
        sisLevelModel.setLevelId(0L);
        sisLevelModel.setLevelNo(0);
        sisLevelModel.setLevelTitle("无等级限制店铺");
        models.add(sisLevelModel);
        return models;
    }

    @Override
    public List<SisLevelConditionsModel> getSisLevelConditionsModels(SisLevel sisLevel) {
        List<SisLevelCondition> sisLevelConditions=sisLevel.getUpgradeConditions();
        List<SisLevelConditionsModel> models=new ArrayList<>();
        if(sisLevelConditions!=null && !sisLevelConditions.isEmpty()){
            for(int i=0,size=sisLevelConditions.size();i<size;i++){
                SisLevelCondition sisLevelCondition=sisLevelConditions.get(i);
                if(sisLevelCondition==null){
                    continue;
                }
                SisLevelConditionsModel sisLevelConditionsModel=new SisLevelConditionsModel();
                sisLevelConditionsModel.setLevelId(sisLevelCondition.getSisLvId());
                SisLevel conditionSisLv=sisLevelRepository.findOne(sisLevelCondition.getSisLvId());
                sisLevelConditionsModel.setLevelTitle(conditionSisLv==null?"无等级限制店铺":conditionSisLv.getLevelName());
                sisLevelConditionsModel.setRelation(sisLevelCondition.getRelation());
                sisLevelConditionsModel.setNum(sisLevelCondition.getNumber());
                models.add(sisLevelConditionsModel);
            }
        }else {
            SisLevelConditionsModel sisLevelConditionsModel=new SisLevelConditionsModel();
            sisLevelConditionsModel.setLevelId(0L);
            sisLevelConditionsModel.setLevelTitle("无等级限制店铺");
            sisLevelConditionsModel.setNum(sisLevel.getUpShopNum());
            sisLevelConditionsModel.setRelation(-1);
            models.add(sisLevelConditionsModel);
        }
        return models;
    }

    @Override
    public Map<Long, Integer> getEachSisLevelNum(User user) {
        Map<Long,Integer> longIntegerMap=new HashMap<>();
        List<Sis> sises=sisRepository.findByUserWhereUserBeloneis(user.getId());
        if(sises!=null&&!sises.isEmpty()){
            for(int i=0,size=sises.size();i<size;i++){
                Sis sis=sises.get(i);
                if(sis.getSisLevel()==null){
                    continue;
                }
                SisLevel sisLevel=sis.getSisLevel();
                Integer number=longIntegerMap.get(sisLevel.getId())==null?0:longIntegerMap.get(sisLevel.getId());
                longIntegerMap.put(sisLevel.getId(),number+1);

                Integer sumNumber=longIntegerMap.get(0L)==null?0:longIntegerMap.get(0L);
                longIntegerMap.put(0L,sumNumber+1);
            }
        }
        return longIntegerMap;
    }

    @Override
    public boolean canToUpgradeSisLevel(User user, SisLevel upSisLevel) {
        //是否达到要求(默认为否)
        boolean canUp=false;
        List<SisLevelCondition> sisLevelConditions=upSisLevel.getUpgradeConditions();


        //兼容老模式的升级
        if(sisLevelConditions==null&&upSisLevel.getUpShopNum()!=null&&upSisLevel.getUpShopNum()>0){
            //获取用户下线的开店数
            Long sisNumber=sisRepository.countSisNum(user.getId());
            if(sisNumber>=upSisLevel.getUpShopNum()){
                canUp=true;
            }
        }else if(sisLevelConditions!=null&&!sisLevelConditions.isEmpty()) {

            //获取用户下线各个等级的开店数量
            Map<Long,Integer> map=getEachSisLevelNum(user);
            if(map.isEmpty()){
                return false;
            }

            for(int i=0,size=sisLevelConditions.size();i<size;i++){
                //获取条件对象
                SisLevelCondition condition=sisLevelConditions.get(i);
                if(condition.getRelation()!=1){
                    if(canUp){
                        break;
                    }
                }
                //根据等级获取人数
                Integer number=map.get(condition.getSisLvId())==null?0:map.get(condition.getSisLvId());
                if(condition.getRelation()!=1){
                    canUp=number>=condition.getNumber();
                }else {
                    canUp=canUp && number>=condition.getNumber();
                }

            }

        }
        return canUp;
    }

    @Override
    public void upgradeAllSisLevel(User user,SisConfig sisConfig) throws Exception {
        if(!sisConfig.getEnableLevelUpgrade()){
            log.info("customer:"+sisConfig.getMerchantId()+" Not open store to upgrade");
            return;
        }

        User beloneOne=userRepository.findOne(user.getBelongOne());
        if(beloneOne!=null){
            upgradeSisLevel(beloneOne);
        }else {
            log.info("user:"+user.getId()+" have no beloneOne");
        }
        log.debug(user.getId()+" belongOne"+user.getBelongOne()+" upgradeOver");
    }

    @Override
    public void upgradeSisLevel(User user) throws Exception {
        User upUser=user;
        while (true){
            boolean isUp=upgradeSisLevelByConditions(upUser);
            if(!isUp){
                break;
            }
            Long belongOneId=upUser.getBelongOne();
            if(belongOneId==null||belongOneId<=0){
                break;
            }
            upUser=userRepository.findOne(belongOneId);
            if(upUser==null){
                break;
            }
        }
    }

    @Override
    public List<SisLevelModel> getSisLevelModels(Long customerId) throws Exception {
        List<SisLevel> sisLevels=sisLevelRepository.findByMerchantId(customerId);
        List<SisLevelModel> sisLevelModels=new ArrayList<>();
        if(sisLevels!=null){
            for(int i=0;i<sisLevels.size();i++){
                SisLevelModel sisLevelModel=new SisLevelModel();
                sisLevelModel.setLevelId(sisLevels.get(i).getId());
                sisLevelModel.setLevelTitle(sisLevels.get(i).getLevelName());
                sisLevelModels.add(sisLevelModel);
            }
        }
        return sisLevelModels;
    }

    @Override
    public SisLevelAward initSisLevelAward(long customerId, long levelId,int layerNum) throws Exception {
        SisLevelAward sisLevelOpenAward=new SisLevelAward();
        sisLevelOpenAward.setBuySisLvId(levelId);

        List<OpenSisAward> openSisAwards=new ArrayList<>();
//        List<SisLevel> sisLevels=sisLevelRepository.findByMerchantId(customerId);
        for(int i=0;i<layerNum;i++){
            OpenSisAward openSisAward=new OpenSisAward();
            openSisAward.setIdx(i);
            openSisAward.setUnified(0);
            openSisAwards.add(openSisAward);
        }
        sisLevelOpenAward.setCfg(openSisAwards);
        return sisLevelOpenAward;
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
        Long sisNumber=sisRepository.countSisNum(user.getId());
        List<SisLevel> sisLevels=sisLevelRepository.findByMerchantIdOrderByLevelNoDesc(user.getMerchant().getId());
        if(sisLevels==null||sisLevels.isEmpty()){
            return null;
        }

        for(SisLevel l:sisLevels){
            if(l.getUpShopNum()==null){
                continue;
            }
            if(sisNumber>=l.getUpShopNum()){
                return l;
            }
        }
        return null;
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
        Sis userSis=sisRepository.findByUser(user);
        if(userSis==null){
            log.info("user:"+user.getId()+" Sis is null");
            return;
        }
        //获取应该升级到的店铺等级
        SisLevel sisLevel= getSisLevelByOfflineSisNum(user);
        if(sisLevel==null||sisLevel.getLevelNo()==null){
            log.info("user:"+user.getId()+" Unable to get the store level");
            return;
        }
        saveSisLevel(userSis,sisLevel);

    }

    @Override
    public boolean upgradeSisLevelByConditions(User user) throws Exception {
        Sis userSis=sisRepository.findByUser(user);
        if(userSis==null){
            log.info("user:"+user.getId()+" Sis is null");
            return false;
        }
        SisLevel userSislevel=userSis.getSisLevel();
        if(userSislevel==null){
            log.info("user:"+user.getId()+" SisLevel is null");
            return false;
        }
        //获取可以升到下一个店铺等级
        SisLevel canUpgradeSisLevel=sisLevelRepository.findFirstByMerchantIdAndLevelNoGreaterThanOrderByLevelNoAsc(
                user.getMerchant().getId(),userSislevel.getLevelNo());
        if(canUpgradeSisLevel==null){
            log.info("user:"+user.getId()+" hava no SisLevel to up");
            return false;
        }
        //判断是否能够升级
        boolean canUp=canToUpgradeSisLevel(user,canUpgradeSisLevel);

        if(canUp){
            //达到要求
            saveSisLevel(userSis,canUpgradeSisLevel);
            return true;
        }
        return false;
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
        List<Goods> goodsList=goodsRepository.findByIdIns(goodsIds,sisConfig.getMerchantId());
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

    @Override
    public List<SisLevel> defaultLevels(Long customerId) throws Exception {
        List<SisLevel> sisLevels=new ArrayList<>();

        List<SisLevel> allSisLevels=sisLevelRepository.findByMerchantIdOrderByLevelNoAsc(customerId);
        if(allSisLevels==null||allSisLevels.isEmpty()){
            return sisLevels;
        }

        List<SisLevel> defaultLevels=new ArrayList<>();
        for(SisLevel sl:allSisLevels){
            if(sl.getIsSystem()!=null&&sl.getIsSystem()==1){
                defaultLevels.add(sl);
            }
        }

        //如果找不到系统等级则取最小等级
        if(defaultLevels.isEmpty()){
            defaultLevels.add(allSisLevels.get(0));
        }
        return defaultLevels;
    }
}
