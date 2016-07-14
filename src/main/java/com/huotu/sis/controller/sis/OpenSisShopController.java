package com.huotu.sis.controller.sis;

/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.Merchant;
import com.huotu.huobanplus.common.entity.MerchantConfig;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.repository.MerchantConfigRepository;
import com.huotu.huobanplus.common.repository.MerchantRepository;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.huobanplus.sdk.mall.annotation.CustomerId;
import com.huotu.sis.common.MathHelper;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.entity.SisOpenAwardAssign;
import com.huotu.sis.entity.support.*;
import com.huotu.sis.model.sis.*;
import com.huotu.sis.model.sisweb.SisLevelModel;
import com.huotu.sis.repository.GoodRepository;
import com.huotu.sis.repository.SisConfigRepository;
import com.huotu.sis.repository.SisLevelRepository;
import com.huotu.sis.repository.SisOpenAwardAssignRepository;
import com.huotu.sis.service.CommonConfigService;
import com.huotu.sis.service.SisConfigService;
import com.huotu.sis.service.SisLevelService;
import com.huotu.sis.service.SisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by slt on 2016/1/22.
 *
 * @author slt
 * @
 */
@Controller
@RequestMapping("/sis")
public class OpenSisShopController {

    @Autowired
    Environment environment;

    @Autowired
    GoodsRestRepository goodsRestRepository;

    @Autowired
    SisConfigService sisConfigService;

    @Autowired
    private SisLevelRepository sisLevelRepository;

    @Autowired
    private GoodRepository goodRepository;

    @Autowired
    SisOpenAwardAssignRepository sisOpenAwardAssignRepository;

    @Autowired
    MerchantRepository merchantRespository;

    @Autowired
    SisConfigRepository sisConfigRepository;

    @Autowired
    CommonConfigService commonConfigService;

    @Autowired
    SisService sisService;

    @Autowired
    MerchantConfigRepository merchantConfigRepository;

    @Autowired
    SisLevelService sisLevelService;

    /**
     * 进入开店设置页面(需要优化)
     *
     * @param customerId 商家ID
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/openConfig", method = RequestMethod.GET)
    public String openConfig(@CustomerId Long customerId, Model model, HttpServletRequest request) throws Exception {
        if (environment.acceptsProfiles("develop"))
        {
            customerId = 4471L;
        }
        if (customerId == null) {
            throw new Exception("商户ID不存在");
        }
        SisConfig sisConfig = sisConfigService.initSisConfig(customerId);
        if (sisConfig != null) {
            if(sisConfig.getOpenGoodsId()!=null) {
                Goods goods = goodRepository.findOne(sisConfig.getOpenGoodsId());
                if(goods!=null){
                    model.addAttribute("goodsTitle", goods.getTitle());
                }

            }
            List<SisLevelModel> sisLevelModels=new ArrayList<>();

            List<SisLevel> sisLevels=sisLevelRepository.findByMerchantId(customerId);
            OpenGoodsIdLevelIds openGoodsIdLevelIds=sisConfig.getOpenGoodsIdlist();
            for(SisLevel sl:sisLevels){
                SisLevelModel sisLevelModel=new SisLevelModel();
                sisLevelModel.setLevelId(sl.getId());
                sisLevelModel.setLevelTitle(sl.getLevelName());
                if(openGoodsIdLevelIds!=null){
                    for(OpenGoodsIdLevelId ogid:openGoodsIdLevelIds.values()){
                        if(sl.getId().equals(ogid.getLevelid())){
                            Goods goods=null;
                            if(ogid.getGoodsid()!=null){
                                goods=goodRepository.findOne(ogid.getGoodsid());
                            }
                            if(goods!=null){
                                sisLevelModel.setGoodsId(goods.getId());
                                sisLevelModel.setGoodsTitle(goods.getTitle());
                                sisLevelModel.setGoodsPrice(goods.getPrice());
                            }
                            break;
                        }
                    }
                }
                sisLevelModels.add(sisLevelModel);
            }

            model.addAttribute("sisLevelModels",sisLevelModels);

        }
        model.addAttribute("newSisConfig", sisConfig);
        model.addAttribute("mainHost",replaceHost(request.getRequestURL().toString()));
        model.addAttribute("mainhost",replaceServerName(request.getServerName()));

        GoodsModel goodsModel=null;
        if(sisConfig.getExtraUpGoodsId()!=null){
            Goods extraUpGoods=goodRepository.findOne(sisConfig.getExtraUpGoodsId());
            if(extraUpGoods!=null){
                goodsModel=new GoodsModel();
                goodsModel.setGoodsId(extraUpGoods.getId());
                goodsModel.setGoodsTitle(extraUpGoods.getTitle());
                goodsModel.setGoodsPrice(extraUpGoods.getPrice());
            }
        }
        model.addAttribute("extraUpGoods",goodsModel);
        return "/sis/openConfig";
    }

    private String replaceServerName(String serverName){
        String[] strings=serverName.split("\\.");
        serverName=serverName.replaceAll(strings[0],"pdmall");
        return serverName;
    }

    /**
     * 替换host域名
     * @param url
     * @return
     */
    private String replaceHost(String url){
        String host=null;
        Pattern p=Pattern.compile("http[s]?://([^:]+)(:\\d+)?/?.*");
        Matcher m=p.matcher(url);
        while(m.find()){
            host=m.group(1);
        }
        String[] strings=host.split("\\.");
        host=host.replaceAll(strings[0],"pdmall");
        return host;
    }

    /**
     * 保存店中店开店设置
     *
     * @param newSisConfig 店中店开店设置实体
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/saveOpenConfig", method = RequestMethod.POST)
    @ResponseBody
    public ResultModel saveOpenConfig(@CustomerId Long customerId, String openGoodsLevels, SisConfig newSisConfig) throws Exception {
        ResultModel resultModel=new ResultModel();
        if (environment.acceptsProfiles("develop")) {
            customerId = 4471L;
        }

        if (customerId == null) {
            throw new Exception("商户ID不存在");
        }
        if (!Objects.isNull(newSisConfig)) {
            newSisConfig.setMerchantId(customerId);
            if(newSisConfig.getOpenGoodsMode()!=null&&newSisConfig.getOpenGoodsMode()==1){//todo 开店商品模式修改
                OpenGoodsIdLevelIdConverter converter=new OpenGoodsIdLevelIdConverter();
                OpenGoodsIdLevelIds openGoodsIdLevelIds=converter.convertToEntityAttribute(openGoodsLevels);
                newSisConfig.setOpenGoodsIdlist(openGoodsIdLevelIds);
            }
            sisConfigService.saveOpenConfig(newSisConfig);
        } else {
            resultModel.setCode(500);
            resultModel.setMessage("保存失败！");
            return resultModel;
        }
        resultModel.setCode(200);
        resultModel.setMessage("保存成功！");
        return resultModel;
    }


    /**
     * 第一批用户资格获取(未使用)
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/userQualification", method = RequestMethod.GET)
    public String userQualification(Model model) throws Exception {
        return "/sis/userQualification";
    }

    /**
     * 进入邀请开店设置(2016/7 新版本启用)
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/inviteConfig", method = RequestMethod.GET)
    public String inviteConfig(@CustomerId Long customerId, Model model) throws Exception {
        if (environment.acceptsProfiles("develop")) {
            customerId = 4471L;
        }

        if (customerId == null) {
            throw new Exception("商户ID不存在");
        }
        SisConfig sisConfig = sisConfigService.initSisConfig(customerId);

        if(StringUtils.isEmpty(sisConfig.getSharePic())){
            sisConfig.setSharePic("");
        }else {
            sisConfig.setSharePic(commonConfigService.getResourcesUri()+sisConfig.getSharePic());
        }
        model.addAttribute("sisConfig", sisConfig);
        return "/sis/inviteConfig";
    }

    /**
     * 保存店中店邀请设置信息(2016/7 新版本启用)
     *
     * @param customerId   商家ID
     * @param newSisConfig 店中店设置表
     * @return 跳转到 /inviteConfig
     * @throws Exception
     */
    @RequestMapping(value = "/saveInviteConfig", method = RequestMethod.POST)
    @ResponseBody
    public ResultModel saveInviteConfig(@CustomerId Long customerId, SisConfig newSisConfig) throws Exception {
        ResultModel resultModel = new ResultModel();
        if (environment.acceptsProfiles("develop")) {
            customerId = 4471L;
        }

        if (customerId == null) {
            throw new Exception("商户ID不存在");
        }
        if (!Objects.isNull(newSisConfig)) {
            //            String contextPath=request.getContextPath();
            newSisConfig.setMerchantId(customerId);
            sisConfigService.saveShareConfig(newSisConfig);
        }
        resultModel.setCode(200);
        resultModel.setMessage("保存成功！");
        return resultModel;
    }


    /**
     * 经营者模式的直推奖设置
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pushAwardConfig", method = RequestMethod.GET)
    public String pushAwardConfig(@CustomerId Long customerId, Model model) throws Exception {
        if(environment.acceptsProfiles("develop")){
            customerId=4471L;
        }
        if(customerId==null){
            throw new Exception("商户ID不存在");
        }
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(customerId);

        List<SisLevel> sisLevels=sisLevelRepository.findByMerchantIdOrderByLevelNoAsc(customerId);
        if(sisLevels.isEmpty()){
            throw new Exception(customerId+"商户没有店中店等级");
        }

        if(sisConfig.getSisRebateTeamManagerSetting()==null){
            //初始化直推奖配置
            sisConfig.setSisRebateTeamManagerSetting(sisConfigService.initSisRebateTeamManagerSetting(customerId));

        }
        double saleAward=sisConfig.getSisRebateTeamManagerSetting().getSaleAward();

        List<RelationAndPercent> manageAwards=sisConfig.getSisRebateTeamManagerSetting().getManageAwards();

        SimpleSisLevelModel[] models=new SimpleSisLevelModel[sisLevels.size()*2];
//        String[] levelNames=new String[sisLevels.size()*2];
        for(int i=0;i<sisLevels.size();i++){
            models[i*2]=new SimpleSisLevelModel();
            models[i*2+1]=new SimpleSisLevelModel();
            models[i*2].setLevelTitle(sisLevels.get(i).getLevelName());
            models[i*2+1].setLevelTitle(sisLevels.get(i).getLevelName());

            models[i*2].setLevelNo(sisLevels.get(i).getLevelNo());
            models[i*2+1].setLevelNo(sisLevels.get(i).getLevelNo());

        }


        model.addAttribute("saleAward",saleAward);

        model.addAttribute("levelNames",models);

        model.addAttribute("manageAwards",manageAwards);

        return "/sis/pushAwardConfig";
    }


    /**
     * 保存经营者模式的直推奖
     * @param customerId
     * @param setting
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/savePushAwardConfig", method = RequestMethod.POST)
    @ResponseBody
    public ResultModel savePushAwardConfig(@CustomerId Long customerId, @RequestBody SisRebateTeamManagerSetting setting) throws Exception {
        ResultModel resultModel = new ResultModel();
        if (environment.acceptsProfiles("develop")) {
            customerId = 4471L;
        }
        if (customerId == null) {
            throw new Exception("商户ID不存在");
        }

        SisConfig sisConfig=sisConfigRepository.findByMerchantId(customerId);

        if(sisConfig!=null&&setting.getManageAwards()!=null){
            sisConfig.setSisRebateTeamManagerSetting(setting);
            sisConfigRepository.save(sisConfig);
        }
        resultModel.setCode(200);
        resultModel.setMessage("保存成功！");
        return resultModel;
    }

    /**
     * 测试经营者模式的直推奖
     * @param customerId
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/testPushAwardConfig",method = RequestMethod.POST)
    @ResponseBody
    public ResultModel testPushAwardConfig(@CustomerId Long customerId,
                                           @RequestBody TestPushAwardModel model) throws Exception{
        ResultModel resultModel = new ResultModel();
        if (environment.acceptsProfiles("develop")) {
            customerId = 4471L;
        }
        if (customerId == null) {
            throw new Exception("商户ID不存在");
        }
        Merchant merchant=merchantRespository.findOne(customerId);
        MerchantConfig merchantConfig =merchantConfigRepository.findByMerchant(merchant);

        //积分兑换钱的比例
        int exchangeRate=merchantConfig.getExchangeRate();

        List<User> users=new ArrayList<>();
        for(Integer n:model.getLevelNos()){
            User user=new User();
            user.setLoginName("test");
            Sis sis=new Sis();
            sis.setUser(user);
        }

        List<Double> percents=sisService.testUserTempIntegralHistoryModel(model.getLevelNos(),model.getSetting().getManageAwards());

        percents.set(0,percents.get(0)+model.getSetting().getSaleAward());
        List<PercentAndIntModel> percentAndIntModels=new ArrayList<>();
        for(int i=0;i<percents.size();i++){
            PercentAndIntModel percentAndIntModel=new PercentAndIntModel();
            double money=model.getAmount()*percents.get(i)/100;
            Integer award=MathHelper.getIntegralRateByRate(money,exchangeRate);
            percentAndIntModel.setPercent(percents.get(i));
            percentAndIntModel.setAward(award);
            percentAndIntModels.add(percentAndIntModel);
        }

        resultModel.setCode(200);
        resultModel.setData(percentAndIntModels);
        resultModel.setMessage("保存成功！");
        return resultModel;


    }


    /**
     * 等级开店的开店奖设置(2016/7新版本弃用)
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/openAwardConfig", method = RequestMethod.GET)
    public String openAwardConfig(@CustomerId Long customerId, Model model) throws Exception {
        if(environment.acceptsProfiles("develop")){
            customerId=4471L;
        }
        if(customerId==null){
            throw new Exception("商户ID不存在");
        }
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(customerId);
        List<SisOpenAwardAssign> sisOpenAwardAssigns=sisOpenAwardAssignRepository.findByMerchant_Id(customerId);
        List<SisLevelOpenAwardModel> sisLevelOpenAwardModels=new ArrayList<>();

        //初始化
        List<SisLevel> sisLevels=sisLevelRepository.findByMerchantIdOrderByLevelNoAsc(customerId);
        for(SisLevel s:sisLevels){
            for(SisLevel os:sisLevels){
                SisLevelOpenAwardModel sisLevelOpenAwardModel=new SisLevelOpenAwardModel();
                sisLevelOpenAwardModel.setLevelId(s.getId());
                sisLevelOpenAwardModel.setLevelName(s.getLevelName());
                sisLevelOpenAwardModel.setOwnLevelId(os.getId());
                sisLevelOpenAwardModel.setOwnLevelName(os.getLevelName());
                sisLevelOpenAwardModels.add(sisLevelOpenAwardModel);

            }
        }
        if(sisOpenAwardAssigns!=null){
            for(SisLevelOpenAwardModel sm:sisLevelOpenAwardModels){
                for(SisOpenAwardAssign s:sisOpenAwardAssigns){
                    if(sm.getLevelId().equals(s.getLevel().getId())&&sm.getOwnLevelId().equals(s.getGuideLevel().getId())){
                        sm.setId(s.getId());
                        sm.setAward(s.getAdvanceVal());
                    }
                }
            }
        }

        model.addAttribute("sisLevelOpenAwardModels",sisLevelOpenAwardModels);
        if(sisConfig!=null){
            model.addAttribute("openAwardMode",sisConfig.getOpenAwardMode());
        }
        return "/sis/openAwardConfig";
    }

    /**
     * 等级开店的保存店中店开店设置(2016/7新版本弃用)
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/saveAwardConfig", method = RequestMethod.POST)
    @ResponseBody
    public ResultModel saveAwardConfig(@CustomerId Long customerId, String levelsAward, Integer type) throws Exception {
        ResultModel resultModel=new ResultModel();
        if (environment.acceptsProfiles("develop")) {
            customerId = 4471L;
        }

        if (customerId == null) {
            throw new Exception("商户ID不存在");
        }
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(customerId);
        if(type==1){
            if(!StringUtils.isEmpty(levelsAward)){
                JSONArray jsonArray=JSON.parseArray(levelsAward);
                for(Object o:jsonArray){
                    SisOpenLevelAwardModel sisOpenLevelAwardModel=JSON.parseObject(o.toString(),SisOpenLevelAwardModel.class);
                    SisOpenAwardAssign sisOpenAwardAssign=
                            sisOpenAwardAssignRepository.findByLevel_IdAndGuideLevel_Id(
                                    sisOpenLevelAwardModel.getLevelId(),sisOpenLevelAwardModel.getOwnlevelId());
                    if(sisOpenAwardAssign!=null){
                        sisOpenAwardAssign.setAdvanceVal(sisOpenLevelAwardModel.getAward());
                    }else {
                        sisOpenAwardAssign=new SisOpenAwardAssign();
                        sisOpenAwardAssign.setMerchant(merchantRespository.findOne(customerId));
                        sisOpenAwardAssign.setLevel(sisLevelRepository.findOne(sisOpenLevelAwardModel.getLevelId()));
                        sisOpenAwardAssign.setGuideLevel(sisLevelRepository.findOne(sisOpenLevelAwardModel.getOwnlevelId()));
                        sisOpenAwardAssign.setAdvanceVal(sisOpenLevelAwardModel.getAward());
                    }
                    sisOpenAwardAssignRepository.save(sisOpenAwardAssign);
                }
            }
        }
        if(sisConfig!=null){
            sisConfig.setOpenAwardMode(type);
            sisConfigRepository.save(sisConfig);
        }

        resultModel.setCode(200);
        resultModel.setMessage("保存成功！");
        return resultModel;
    }





    /**
     * 获取商品分类列表(未使用)
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sortsList", method = RequestMethod.GET)
    @ResponseBody
    public ResultModel sortsList(@CustomerId Long customerId) throws Exception {
        if (environment.acceptsProfiles("develop")) {
            customerId = 4471L;
        }

        if (customerId == null) {
            throw new Exception("商户ID不存在");
        }
        ResultModel resultModel = new ResultModel();
        //获取分类列表
        List<CategoryModel> categoryModels = sisConfigService.getCategory(customerId);
        resultModel.setCode(1);
        resultModel.setData(categoryModels);
        return resultModel;
    }


    /**
     * 获取商品列表
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/goodsList", method = RequestMethod.GET)
    @ResponseBody
    public ResultModel goodsList(@CustomerId Long customerId, String title, String path, Integer pageNo) throws Exception {
        if (environment.acceptsProfiles("develop")) {
            customerId = 4471L;
        }
        if (customerId == null) {
            throw new Exception("商户ID不存在");
        }


        ResultModel resultModel = new ResultModel();
        //获取商品列表
        Page<MallGoodModel> goodses = sisConfigService.getMallGood(customerId, title, pageNo);
        resultModel.setCode(1);
        resultModel.setData(Objects.isNull(goodses) ? null : goodses.getContent());
        resultModel.setMessage(String.valueOf(goodses.getTotalPages()));//总页数
        return resultModel;
    }


    /**
     * by zyw 店铺等级设置列表
     *
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/levelSet", method = RequestMethod.GET)
    public String levelSet(@CustomerId Long customerId, Model model) throws Exception {
        if (environment.acceptsProfiles("develop")) {
            customerId = 4471L;
        }
        if (customerId == null) {
            throw new Exception("商户ID不存在");
        }
        List<SisLevel> sisLevels = sisLevelRepository.findByMerchantId(customerId);
        List<SisLevelSetModel> sisLevelSetModels = new ArrayList<SisLevelSetModel>();
        Collections.sort(sisLevels, (SisLevel object1, SisLevel object2) -> object1.getLevelNo().compareTo(object2.getLevelNo()));

        for (int i = 0; i < sisLevels.size(); i++) {
            SisLevelSetModel sisLevelSetModel = new SisLevelSetModel();
            SisLevel sisLevel=sisLevels.get(i);
            sisLevelSetModel.setId(sisLevel.getId().intValue());
            sisLevelSetModel.setLevel(sisLevel.getLevelNo());
            sisLevelSetModel.setCustomerId(sisLevel.getMerchantId());
            sisLevelSetModel.setTuanduidianpuAmount(sisLevel.getUpTeamShopNum());
            sisLevelSetModel.setZhituidianpuAmount(sisLevel.getUpShopNum());
            List<SisLevelConditionsModel> sisLevelConditionsModels=sisLevelService.getSisLevelConditionsModels(sisLevel);
            sisLevelSetModel.setUpgradeConditions(sisLevelConditionsModels.toString());
            sisLevelSetModel.setNickname(sisLevel.getLevelName());
            sisLevelSetModel.setRebate(sisLevel.getRebateRate());
            sisLevelSetModel.setIsSystem(sisLevel.getIsSystem());
            sisLevelSetModel.setExtraUpgrade(sisLevel.getExtraUpgrade());
            sisLevelSetModels.add(sisLevelSetModel);
        }

        model.addAttribute("sisLevelSetModels", sisLevelSetModels);
        return "/sis/newLevelSet";
    }


    /**
     * 添加等级页面初始化
     *
     * @param customerId
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/jumpToAddLevelSet", method = RequestMethod.GET)
    public String jumpToAddLevelSet(@CustomerId Long customerId,SisLevel sisLevel, Model model) throws
            Exception {
        if (environment.acceptsProfiles("develop")) {
            customerId = 4471L;
        }
        if (customerId == null) {
            throw new Exception("商户ID不存在");
        }
//        List<SisLevelSetModel> sisLevelSetModels = new ArrayList<SisLevelSetModel>();
//        SisLevelSetModel sisLevelSetModel = new SisLevelSetModel();
//        sisLevelSetModel.setNickname("请输入对应的值");
//        sisLevelSetModels.add(sisLevelSetModel);
//        model.addAttribute("sisLevelSetModels", sisLevelSetModels);
//        List<SisLevel> sisLevels=sisLevelRepository.findByMerchantId(customerId);
        Integer levelNo=0;
        Object topLevel=sisLevelRepository.findTopSisLevel(customerId);
        if(topLevel!=null){
            levelNo=Integer.parseInt(String.valueOf(topLevel))+1;
        }
        sisLevel.setLevelNo(levelNo);
        model.addAttribute("sisLevel", sisLevel);
        model.addAttribute("sisLvs",sisLevelService.getSimpleSisLevelModel(customerId,sisLevel.getLevelNo()));
        model.addAttribute("conditions",sisLevelService.getSisLevelConditionsModels(sisLevel));
//        model.addAttribute("sisLevels",)
        return "/sis/newAddLevelSet";
    }


    /**
     * 店铺等级添加保存
     * 条件：必须唯一，不存在
     * 在上下级之间数据
     *
     * @param customerId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/saveLevelSetConfig", method = RequestMethod.POST)
    public String saveLevelSetConfig(@CustomerId Long customerId, String level, String amount1, String amount2, String nickname, String rebate, Model
            model, String extraUpgrade) throws Exception {
        if (environment.acceptsProfiles("develop")) {
            customerId = 4471L;
        }
        if (customerId == null) {
            throw new Exception("商户ID不存在");
        }

        List<SisLevel> sisLevels = sisLevelRepository.findByMerchantId(customerId);
        Collections.sort(sisLevels, (SisLevel object1, SisLevel object2) -> object1.getLevelNo().compareTo(object2.getLevelNo()));
        int order = 0;

        for (int i = 0; i < sisLevels.size(); i++) {
            if (sisLevels.get(i).getLevelNo() == Integer.parseInt(level)) {
                List<SisLevelSetModel> sisLevelSetModels = new ArrayList<SisLevelSetModel>();
                SisLevelSetModel sisLevelSetModel = new SisLevelSetModel();
                sisLevelSetModel.setNickname("店铺等级已经存在,请重新输入");
                sisLevelSetModels.add(sisLevelSetModel);
                model.addAttribute("sisLevelSetModels", sisLevelSetModels);
                return "/sis/addLevelSet";
            }
        }
        /**
         * 获取新增的记录需要加入的位置在哪里
         */
        for (int j = 0; j < sisLevels.size() - 1; j++) {
            if (((Integer.parseInt(level)) < (sisLevels.get(j + 1).getLevelNo())) && ((Integer.parseInt(level)) > (sisLevels.get(j).getLevelNo()))) {
                order = j;
                break;
            } else if (Integer.parseInt(level) > sisLevels.get(sisLevels.size() - 1).getLevelNo()) {
                order = j + 1;

            }
        }
        /**
         * 新增一条记录 而且该记录位于列表的中间的情况
         */
        if (order != (sisLevels.size() - 1) && Integer.parseInt(amount1) > sisLevels.get(order).getUpTeamShopNum() &&
                Integer.parseInt(amount1) < sisLevels.get(order + 1).getUpTeamShopNum()
                && Integer.parseInt(amount2) > sisLevels.get(order).getUpShopNum() && Integer.parseInt(amount2) < sisLevels.get(order + 1).getUpShopNum()
                && Double.parseDouble(rebate) > sisLevels.get(order).getRebateRate() && Double.parseDouble(rebate) < sisLevels.get(order + 1).getRebateRate()) {
            SisLevel sisLevelNew = new SisLevel();
            sisLevelNew.setIsSystem(0);
            sisLevelNew.setLevelNo(Integer.parseInt(level));
            sisLevelNew.setUpShopNum(Integer.parseInt(amount2));
            sisLevelNew.setMerchantId(customerId);
            sisLevelNew.setUpTeamShopNum(Integer.parseInt(amount1));
            sisLevelNew.setLevelName(nickname);
            sisLevelNew.setRebateRate(Integer.parseInt(rebate));
            sisLevelNew.setExtraUpgrade(Integer.parseInt(extraUpgrade));
            sisLevelRepository.save(sisLevelNew);
            return "redirect:levelSet";

        }
        /**
         * 新增一条记录  而且该记录位于列表的最下面的情况
         */
        if ((order == sisLevels.size() - 1) && Integer.parseInt(amount1) > sisLevels.get(order).getUpTeamShopNum() && Integer.parseInt(amount2) >
                sisLevels.get(order).getUpShopNum() && Double.parseDouble(rebate) > sisLevels.get(order).getRebateRate()) {

            SisLevel sisLevelNew = new SisLevel();
            sisLevelNew.setIsSystem(0);
            sisLevelNew.setLevelNo(Integer.parseInt(level));
            sisLevelNew.setUpShopNum(Integer.parseInt(amount2));
            sisLevelNew.setMerchantId(customerId);
            sisLevelNew.setUpTeamShopNum(Integer.parseInt(amount1));
            sisLevelNew.setLevelName(nickname);
            sisLevelNew.setRebateRate(Integer.parseInt(rebate));
            sisLevelNew.setExtraUpgrade(Integer.parseInt(extraUpgrade));

            sisLevelRepository.save(sisLevelNew);

            return "redirect:levelSet";

        } else {
            List<SisLevelSetModel> sisLevelSetModels = new ArrayList<SisLevelSetModel>();
            SisLevelSetModel sisLevelSetModel = new SisLevelSetModel();
            sisLevelSetModel.setNickname("店铺数量或直推奖输入错误,请重新输入,应该从小到大");
            sisLevelSetModels.add(sisLevelSetModel);
            model.addAttribute("sisLevelSetModels", sisLevelSetModels);
        }
        return "/sis/addLevelSet";
    }


    /**
     * 保存店中店等级
     * @param newSisLevel
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/saveSisLevel",method = RequestMethod.POST)
    @ResponseBody
    public ResultModel saveSisLevel(@CustomerId Long customerId,@RequestBody SisLevel newSisLevel) throws Exception{
        ResultModel resultModel=new ResultModel();
        if (environment.acceptsProfiles("develop")) {
            customerId = 4471L;
        }
        if (customerId == null) {
            resultModel.setCode(500);
            resultModel.setMessage("商户ID不存在");
            return resultModel;

        }
        if(newSisLevel==null){
            resultModel.setCode(500);
            resultModel.setMessage("没有店铺信息");
            return resultModel;
        }

        //条件有效性判断

        if(newSisLevel.getId()==null){
            newSisLevel.setMerchantId(customerId);

            //经营者配置修改
            SisConfig sisConfig=sisConfigRepository.findByMerchantId(customerId);
            SisRebateTeamManagerSetting setting=sisConfig.getSisRebateTeamManagerSetting();
            if(setting!=null){
                List<RelationAndPercent> relationAndPercents=setting.getManageAwards();
                Object topLevel=sisLevelRepository.findTopSisLevel(customerId);
                if(topLevel!=null){
                    Integer levelNo=Integer.parseInt(String.valueOf(topLevel));
                    RelationAndPercent relationAndPercent=new RelationAndPercent();
                    relationAndPercent.setPercent(0);
                    String relation=levelNo+"_"+newSisLevel.getLevelNo();
                    relationAndPercent.setRelation(relation);
                    relationAndPercents.add(relationAndPercent);

                    relationAndPercent=new RelationAndPercent();
                    relationAndPercent.setPercent(0);
                    relation=newSisLevel.getLevelNo()+"_"+newSisLevel.getLevelNo();
                    relationAndPercent.setRelation(relation);
                    relationAndPercents.add(relationAndPercent);

                    setting.setManageAwards(relationAndPercents);
                    sisConfig.setSisRebateTeamManagerSetting(setting);
                    sisConfigRepository.save(sisConfig);
                }
            }
        }else {
            SisLevel oldSisLevel=sisLevelRepository.findOne(newSisLevel.getId());
            newSisLevel.setMerchantId(oldSisLevel.getMerchantId());
            newSisLevel.setRrmark(oldSisLevel.getRrmark());
            newSisLevel.setUpTeamShopNum(oldSisLevel.getUpTeamShopNum());
//            newSisLevel.setUpShopNum(oldSisLevel.getUpShopNum());
//            newSisLevel.setRebateRate(oldSisLevel.getRebateRate());
            newSisLevel.setIsSystem(oldSisLevel.getIsSystem());


        }
        sisLevelRepository.save(newSisLevel);

        resultModel.setCode(200);
        resultModel.setMessage("保存成功！");
        return resultModel;
    }

    //修改店铺信息(弃用)
    @RequestMapping(value = "/saveLevelSetConfig1", method = RequestMethod.POST)
    public String saveLevelSetConfig1(@CustomerId Long customerId, HttpServletRequest httpServletRequest, String
            level,
                                      String amount1, String amount2, String nickname, String rebate, Model model,
                                      String extraUpgrade) throws Exception {

        int extraUpgrade2=0;
        if(extraUpgrade.equals("已启用补差价")||extraUpgrade.equals("启用补差价")){
            extraUpgrade2=1;

        }else if(extraUpgrade.equals("不启用补差价")){
            extraUpgrade2=0;
        }
        if (environment.acceptsProfiles("develop")) {
            customerId = 4471L;
        }
        if (customerId == null) {
            throw new Exception("商户ID不存在");
        }
        List<SisLevel> sisLevels = sisLevelRepository.findByMerchantId(customerId);
        Collections.sort(sisLevels, (SisLevel object1, SisLevel object2) -> object1.getLevelNo().compareTo(object2.getLevelNo()));
        int order = 0;
        Long id = Long.parseLong(httpServletRequest.getParameter("id"));
        SisLevel sisLevelNew = sisLevelRepository.findOne(id);
        sisLevelNew.setLevelNo(Integer.parseInt(level));

        /**
         * 获取所需要修改的记录的位置  order的值指该记录所在的位置信息
         */
        for (int j = 0; j < sisLevels.size() - 1; j++) {
            if (((Integer.parseInt(level)) < (sisLevels.get(j + 1).getLevelNo()))) {
                order = j;
                break;
            } else if (Integer.parseInt(level) > sisLevels.get(sisLevels.size() - 2).getLevelNo()) {
                order = j + 2;

            }
        }
        /**
         * 所要修改的字段位于列表的中间  判断所修改的店铺数量与其上下的大小是否符合规定
         */
        if (order > 0 && order != (sisLevels.size()) && Integer.parseInt(amount1) > sisLevels.get(order - 1).getUpTeamShopNum() &&
                Integer.parseInt(amount1) < sisLevels.get(order + 1).getUpTeamShopNum() && Integer.parseInt(amount2) >
                sisLevels.get(order - 1).getUpShopNum() && Integer.parseInt(amount2) < sisLevels.get(order + 1)
                .getUpShopNum() && Double.parseDouble(rebate) > sisLevels.get(order - 1).getRebateRate() && Double
                .parseDouble(rebate) < sisLevels.get(order + 1).getRebateRate()) {

            sisLevelNew.setIsSystem(0);
            sisLevelNew.setLevelNo(Integer.parseInt(level));
            sisLevelNew.setUpShopNum(Integer.parseInt(amount2));
            sisLevelNew.setMerchantId(customerId);
            sisLevelNew.setUpTeamShopNum(Integer.parseInt(amount1));
            sisLevelNew.setLevelName(nickname);
            sisLevelNew.setRebateRate(Double.parseDouble(rebate));

            sisLevelNew.setExtraUpgrade(extraUpgrade2);
            sisLevelRepository.save(sisLevelNew);
            return "redirect:levelSet";

        }
        if (order == 0) {
            sisLevelNew.setIsSystem(1);
            sisLevelNew.setLevelNo(Integer.parseInt(level));
            sisLevelNew.setUpShopNum(Integer.parseInt(amount2));
            sisLevelNew.setMerchantId(customerId);
            sisLevelNew.setUpTeamShopNum(Integer.parseInt(amount1));
            sisLevelNew.setLevelName(nickname);
            sisLevelNew.setRebateRate(Double.parseDouble(rebate));
            sisLevelNew.setExtraUpgrade(extraUpgrade2);
            sisLevelRepository.save(sisLevelNew);

            return "redirect:levelSet";

        }
        /**
         * 修改店铺信息  要求直推店铺数量和团队店铺数量符合递增的规则 且修改的字段位于列表的最下方
         */
        if ((order == sisLevels.size()) && Integer.parseInt(amount1) > sisLevels.get(order - 2).getUpTeamShopNum() &&
                Integer.parseInt(amount2) > sisLevels.get(order - 2).getUpShopNum() && Double.parseDouble(rebate)
                > sisLevels.get(order - 2).getRebateRate()) {
            sisLevelNew.setIsSystem(0);
            sisLevelNew.setLevelNo(Integer.parseInt(level));
            sisLevelNew.setUpShopNum(Integer.parseInt(amount2));
            sisLevelNew.setMerchantId(customerId);
            sisLevelNew.setUpTeamShopNum(Integer.parseInt(amount1));
            sisLevelNew.setLevelName(nickname);
            sisLevelNew.setRebateRate(Double.parseDouble(rebate));
            sisLevelNew.setExtraUpgrade(extraUpgrade2);
            sisLevelRepository.save(sisLevelNew);

            return "redirect:levelSet";

        } else {

            SisLevel sisLevel = sisLevelRepository.findOne(id);

            sisLevel.setRrmark("数据输入不合法 请重新输入");
            model.addAttribute("sisLevel", sisLevel);
        }

        return "/sis/updateLevelSet";

    }


    /**
     * 删除需要条件
     * 有对应商铺的情况下不能删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/jumpToDeleteLevelSet", method = RequestMethod.GET)
    public String jumpToDeleteLevelSet(Long id) throws Exception {
        Long newId = id;
        sisLevelRepository.delete(newId);
        return "redirect:levelSet";
    }

    /**
     * 调整到编辑页面
     *  负责人：史利挺
     * @param id            店铺等级ID
     * @param model         返回的model
     * @param customerId    商户ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/jumpToChangeLevelSet", method = RequestMethod.GET)
    public String jumpToChangeLevelSet(@CustomerId Long customerId,Long id, Model model) throws Exception {
        SisLevel sisLevel = sisLevelRepository.findOne(id);
        //如果升级条件为null，处理数据
//        List<SisLevelCondition> sisLevelConditions= sisLevel.getUpgradeConditions();
//        List<SisLevelConditionsModel> models=new ArrayList<>();
//
//        if(sisLevelConditions!=null){
//            for(int i=0,size=sisLevelConditions.size();i<size;i++){
//                SisLevelCondition sisLevelCondition=sisLevelConditions.get(i);
//                if(sisLevelCondition==null){
//                    continue;
//                }
//                SisLevelConditionsModel sisLevelConditionsModel=new SisLevelConditionsModel();
//                sisLevelConditionsModel.setLevelId(sisLevelCondition.getSisLvId());
//                SisLevel conditionSisLv=sisLevelRepository.findOne(sisLevelCondition.getSisLvId());
//                sisLevelConditionsModel.setLevelTitle(conditionSisLv==null?"无等级限制店铺":conditionSisLv.getLevelName());
//                sisLevelConditionsModel.setRelation(sisLevelCondition.getRelation());
//                sisLevelConditionsModel.setNum(sisLevelCondition.getNumber());
//                models.add(sisLevelConditionsModel);
//            }
//        }else {
//
//        }
        model.addAttribute("sisLvs",sisLevelService.getSimpleSisLevelModel(customerId,sisLevel.getLevelNo()));
        model.addAttribute("sisLevel", sisLevel);
        model.addAttribute("conditions",sisLevelService.getSisLevelConditionsModels(sisLevel));
        return "/sis/newAddLevelSet";
    }
}
