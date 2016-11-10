package com.huotu.sis.service.impl;

import com.huotu.huobanplus.common.entity.Category;
import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.sdk.common.repository.CategoryRestRepository;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.entity.support.OpenGoodsIdLevelId;
import com.huotu.sis.entity.support.OpenGoodsIdLevelIds;
import com.huotu.sis.entity.support.RelationAndPercent;
import com.huotu.sis.entity.support.SisRebateTeamManagerSetting;
import com.huotu.sis.model.sis.CategoryModel;
import com.huotu.sis.model.sis.MallGoodModel;
import com.huotu.sis.repository.SisConfigRepository;
import com.huotu.sis.repository.SisLevelRepository;
import com.huotu.sis.repository.mall.GoodsRepository;
import com.huotu.sis.service.SisConfigService;
import com.huotu.sis.service.SisLevelService;
import com.huotu.sis.service.StaticResourceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2016/1/26.
 */
@Service
public class SisConfigServiceImpl implements SisConfigService {
    private Log log = LogFactory.getLog(SisConfigServiceImpl.class);
    @Autowired
    Environment environment;

    @Autowired
    SisConfigRepository sisConfigRepository;

    @Autowired
    StaticResourceService staticResourceService;

    @Autowired
    CategoryRestRepository categoryRestRepository;

    @Autowired
    GoodsRestRepository goodsRestRepository;

    @Autowired
    GoodsRepository goodRepository;

    @Autowired
    SisLevelRepository sisLevelRepository;

    @Autowired
    SisLevelService sisLevelService;

    @Override
    public SisConfig initSisConfig(Long customerId) throws Exception {//todo 初始化
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(customerId);
        if(Objects.isNull(sisConfig)){
            sisConfig=new SisConfig();
            sisConfig.setEnabled(1);
            sisConfig.setMerchantId(customerId);
            sisConfig.setOpenMode(1);
            sisConfig.setOpenAwardMode(0);
            sisConfig.setEnableLevelUpgrade(false);
            sisConfig.setOpenGoodsMode(1);
            sisConfig.setCorpStockSelf(0);
            sisConfig.setGoodSelectMode(0);
            sisConfig.setHomePageColor("#FF5BA0");
            sisConfig.setCorpStockBelongOne(0);
            sisConfig.setOpenNeedInvite(1);
            sisConfig.setSisShopMode(0);
            sisConfig.setMaxMartketableNum(100);
            sisConfig.setMaxBrandNum(5);
            sisConfig.setPushAwardMode(0);
            sisConfig.setUpdateTime(new Date());
            sisConfig.setShareTitle("[name]邀请您加入[mgt]");
            sisConfig.setShareDesc("点击注册，开启你的创业之路！");
            sisConfig.setSharePic(staticResourceService.getResource(staticResourceService.IMG+"sharePic.jpg").toString());
        }
        return sisConfig;
    }

    @Override
    public void saveShareConfig(SisConfig newSisConfig) throws Exception {
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(newSisConfig.getMerchantId());
        if(Objects.isNull(sisConfig)){
            sisConfig=initSisConfig(newSisConfig.getMerchantId());
        }
        sisConfig.setShareTitle(newSisConfig.getShareTitle());
        sisConfig.setShareDesc(newSisConfig.getShareDesc());
        sisConfig.setSharePic(newSisConfig.getSharePic());
        sisConfig.setContent(newSisConfig.getContent());
        sisConfig.setUpdateTime(new Date());
        sisConfigRepository.save(sisConfig);
    }

    @Override
    public void saveOpenConfig(SisConfig newSisConfig) throws Exception {
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(newSisConfig.getMerchantId());
        if(Objects.isNull(sisConfig)){
            sisConfig=initSisConfig(newSisConfig.getMerchantId());
        }
        sisConfig.setEnabled(newSisConfig.getEnabled());
        sisConfig.setOpenNeedInvite(newSisConfig.getOpenNeedInvite());
        sisConfig.setOpenMode(newSisConfig.getOpenMode());
        sisConfig.setPushAwardMode(newSisConfig.getPushAwardMode());
        sisConfig.setHomePageColor(newSisConfig.getHomePageColor());
        sisConfig.setMaxMartketableNum(newSisConfig.getMaxMartketableNum());
        sisConfig.setMaxBrandNum(newSisConfig.getMaxBrandNum());
        sisConfig.setGoodSelectMode(newSisConfig.getGoodSelectMode());
        sisConfig.setSisShopMode(newSisConfig.getSisShopMode());
        sisConfig.setLimitShelvesNum(newSisConfig.getLimitShelvesNum());
        sisConfig.setUpdateTime(new Date());
        sisConfigRepository.save(sisConfig);

    }

    @Override
    public List<CategoryModel> getCategory(Long customerId) throws IOException {
        List<Category> categories;
        try{
            categories=categoryRestRepository.findByMerchantId(customerId, new PageRequest(0, Integer.MAX_VALUE)).getContent();
        }catch (Exception ex){
            return null;
        }
        //如果有分类数据
        if(!Objects.isNull(categories)&&!categories.isEmpty()){
            List<CategoryModel>categoryModels=new ArrayList<>();
            for(int i=0;i<categories.size();i++){
                Category category=categories.get(i);
                Long id=category.getId();
                CategoryModel categoryModel=new CategoryModel();
                //将分类路径拆分成id数组
                String[] ids=category.getCatPath().split("\\|");
                categoryModel.setId(id);
                categoryModel.setTitle(category.getTitle());
                categoryModel.setCatPath(category.getCatPath());
                categoryModel.setDepth(ids.length-2);
                categoryModels.add(categoryModel);


            }
            return categoryModels;
        }

        return null;
    }

    @Override
    public Page<MallGoodModel> getMallGood(Long customerId, String catPath, String title, Integer pageNo) throws IOException {
        catPath=catPath+"%";
        catPath= URLEncoder.encode(catPath,"UTF8");
        title= StringUtils.isEmpty(title.trim())?null:"%"+title+"%";
        if(!StringUtils.isEmpty(title)){
            title=URLEncoder.encode(title,"UTF8");
        }
        Page<Goods> goodses=goodsRestRepository.findByTitleAndCategory(title, catPath, customerId, 0, new PageRequest(pageNo, 10));
        List<MallGoodModel> mallGoodModels=new ArrayList<>();
        if(!Objects.isNull(goodses)){
            for(Goods g:goodses){
                MallGoodModel mallGoodModel=new MallGoodModel();
                mallGoodModel.setId(g.getId());
                mallGoodModel.setTitle(g.getTitle());
                mallGoodModel.setImg(g.getSmallPic().getValue());
                mallGoodModel.setOriginalPrice(g.getPrice());
                mallGoodModel.setPrice(g.getPrice());
                mallGoodModel.setIntegral(0);//todo
                mallGoodModels.add(mallGoodModel);
            }
            Page<MallGoodModel> mallGoodModelPage=new PageImpl<MallGoodModel>(mallGoodModels,new PageRequest(pageNo,10),goodses.getTotalElements());
            return mallGoodModelPage;
        }
        return null;
    }

    @Override
    public Page<MallGoodModel> getMallGood(Long customerId, String title, Integer pageNo) throws IOException {
        Page goodses=goodRepository.findByOwnerAndTitleLike(customerId,"%"+title+"%",new PageRequest(pageNo,10));
        List<MallGoodModel> goodsModels=new ArrayList<>();
        goodses.forEach(r->{
            Object[] obj = (Object[]) r;
            MallGoodModel simpleGoodsModel=new MallGoodModel();
            simpleGoodsModel.setId(obj[0]==null?0:Long.parseLong(obj[0].toString()));
            simpleGoodsModel.setTitle(obj[1]==null?"":obj[1].toString());
            simpleGoodsModel.setPrice(obj[2]==null?0:Double.parseDouble(obj[2].toString()));
            goodsModels.add(simpleGoodsModel);
        });
        return new PageImpl<>(goodsModels,new PageRequest(pageNo,10),goodses.getTotalElements());
    }

    @Override
    public SisRebateTeamManagerSetting initSisRebateTeamManagerSetting(Long merchantId) throws Exception {
        SisRebateTeamManagerSetting setting=new SisRebateTeamManagerSetting();
        setting.setSaleAward(0);
        List<SisLevel> sisLevels=sisLevelRepository.findByMerchantIdOrderByLevelNoAsc(merchantId);
        List<RelationAndPercent> relationAndPercents=new ArrayList<>();
        //数组循环下标
        int n=0;
        //循环数组
        int[] sub={0,0};
        while (true){
            SisLevel left=sisLevels.get(sub[0]);
            SisLevel right=sisLevels.get(sub[1]);
            RelationAndPercent relationAndPercent=new RelationAndPercent();
            relationAndPercent.setRelation(left.getLevelNo()+"_"+right.getLevelNo());
            relationAndPercent.setPercent(0);
            relationAndPercents.add(relationAndPercent);
            if(sub[(n+1)%2]+1>=sisLevels.size()){
                break;
            }
            sub[++n%2]++;
        }
        setting.setManageAwards(relationAndPercents);
        return setting;
    }

    @Override
    public void compatibilityOpenShopGoods(Long customerId) throws Exception {
        if(customerId==null){
            log.info("have no customerId,compatibilityOpenShopGoods fail");
            return;
        }
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(customerId);
        if(sisConfig==null){
            log.info("have no sisConfig,compatibilityOpenShopGoods fail");
            return;
        }

        OpenGoodsIdLevelIds openGoodsIdLevelIds= sisConfig.getOpenGoodsIdlist();
        List<SisLevel> needInitSisLevels=sisLevelService.defaultLevels(customerId);
        if(needInitSisLevels==null||needInitSisLevels.isEmpty()){
            log.info("have no needInitSisLevels,compatibilityOpenShopGoods fail");
            return;
        }

        if(openGoodsIdLevelIds==null){
            openGoodsIdLevelIds=new OpenGoodsIdLevelIds();
        }

        for(SisLevel sl:needInitSisLevels){
            OpenGoodsIdLevelId openGoodsIdLevelId=openGoodsIdLevelIds.get(sl.getId());
            if(openGoodsIdLevelId==null){
                openGoodsIdLevelId=new OpenGoodsIdLevelId();
                openGoodsIdLevelId.setLevelid(sl.getId());
                openGoodsIdLevelId.setGoodsid(sisConfig.getOpenGoodsId());
                openGoodsIdLevelIds.put(sl.getId(),openGoodsIdLevelId);
            }
        }
        sisConfig.setOpenGoodsIdlist(openGoodsIdLevelIds);
        sisConfigRepository.save(sisConfig);
    }

    @Override
    public void defaultLevelSelected(Long customerId) throws Exception {
        List<SisLevel> defaultLevels=sisLevelService.defaultLevels(customerId);
        for(SisLevel sisLevel:defaultLevels){
            if(sisLevel.getExtraUpgrade()==null||sisLevel.getExtraUpgrade()==0){
                sisLevel.setExtraUpgrade(1);
                sisLevelRepository.save(sisLevel);
            }
        }

    }

    @Override
    public void compatibilityOpenShopGoodsAndSelected(Long customerId) throws Exception {
        compatibilityOpenShopGoods(customerId);
        defaultLevelSelected(customerId);
    }
}
