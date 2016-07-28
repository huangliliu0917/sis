package com.huotu.sis.controller.sis;

/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huotu.huobanplus.sdk.mall.annotation.CustomerId;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.entity.support.*;
import com.huotu.sis.exception.SisException;
import com.huotu.sis.model.sis.ResultModel;
import com.huotu.sis.repository.SisConfigRepository;
import com.huotu.sis.repository.SisLevelRepository;
import com.huotu.sis.service.SisLevelService;
import com.huotu.sis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺等级相关配置
 * Created by jinzj on 2016/1/21.
 * modify by slt    on 2016/7/19
 *
 * @author jinzj
 * @
 */
@Controller
@RequestMapping("/sis")
public class SisLevelController {
    @Autowired
    private SisConfigRepository sisConfigRepository;

    @Autowired
    private SisLevelService sisLevelService;

    @Autowired
    private SisLevelRepository sisLevelRepository;

    @Autowired
    private UserService userService;

    /**
     * 显示返利配置页面
     * @param customerId
     * @param levelId
     * @param type      返利类型 0：开店奖，1：直推奖，2：梦想股
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping("/showLevelAwardConfig")
    public String showLevelAwardConfig(@CustomerId Long customerId,
                                           @RequestParam(required = true) Long levelId,
                                           @RequestParam(required = true) Integer type,
                                           Model model) throws Exception{
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(customerId);
        if(sisConfig==null){
            throw new SisException("customer "+customerId+" have no SisConfig");
        }
        int layerNum=4;
        String unit="";
        boolean individuality=false;
        SisLevelAwards sisLevelAwards=new SisLevelAwards();
        //获取开店配置
        switch (type){
            case 0:
                sisLevelAwards=sisConfig.getSisLevelOpenAwards();
                if(sisLevelAwards==null){
                    sisLevelAwards=userService.oldOpenAwardCompatibility(sisConfig);
                }
                unit="元";
                individuality=true;
                break;
            case 1:
                sisLevelAwards=sisConfig.getSisLevelPushAwards();
                unit="%";
                break;
            case 2:
                sisLevelAwards=sisConfig.getSisLevelStockAwards();
                if(sisLevelAwards==null){
                    sisLevelAwards=userService.oldStockAwardCompatibility(levelId,sisConfig);
                }
                unit="股";
                layerNum=2;
                break;
            default:
        }

        SisLevelAward sisLevelAward=new SisLevelAward();
        if(sisLevelAwards!=null){
            sisLevelAward=sisLevelAwards.get(levelId);
        }
        //初始化sisLevelAward
        if(sisLevelAward==null||sisLevelAward.getCfg()==null){
            sisLevelAward=sisLevelService.initSisLevelAward(customerId,levelId,layerNum);
        }
        model.addAttribute("unit",unit);
        model.addAttribute("individuality",individuality);
        model.addAttribute("layerNum",layerNum);
        model.addAttribute("sislevels",sisLevelService.getSimpleSisLevelModel(customerId));
        model.addAttribute("cfgs",sisLevelAward.getCfg());
        model.addAttribute("levelId",sisLevelAward.getBuySisLvId());
        model.addAttribute("type",type);
        return "sis/newAwardConfig";


    }

    /**
     * 保存返利
     * @param customerId        商户ID
     * @param sisLevelAwardStr  返利的json字符串
     * @param type              保存返利的类型
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/saveLevelAwardConfig",method = RequestMethod.POST)
    @ResponseBody
    public ResultModel saveLevelAwardConfig(@CustomerId Long customerId,
                                                 String sisLevelAwardStr,Integer type) throws Exception{
        ResultModel resultModel=new ResultModel();
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(customerId);
        if(sisConfig==null){
            resultModel.setCode(500);
            resultModel.setMessage("customer "+customerId+" have no SisConfig");
            return resultModel;
        }
        SisLevelAwards sisLevelAwards=new SisLevelAwards();
        switch (type){
            case 0:
                sisLevelAwards=sisConfig.getSisLevelOpenAwards();
                break;
            case 1:
                sisLevelAwards=sisConfig.getSisLevelPushAwards();
                break;
            case 2:
                sisLevelAwards=sisConfig.getSisLevelStockAwards();
                break;
            default:
        }

        if(sisLevelAwards==null){
            sisLevelAwards=new SisLevelAwards();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        SisLevelAward sisLevelAward=objectMapper.treeToValue(objectMapper.readTree(sisLevelAwardStr),SisLevelAward.class);
        sisLevelAwards.put(sisLevelAward.getBuySisLvId(),sisLevelAward);

        switch (type){
            case 0:
                sisConfig.setSisLevelOpenAwards(sisLevelAwards);
                break;
            case 1:
                sisConfig.setSisLevelPushAwards(sisLevelAwards);
                break;
            case 2:
                sisConfig.setSisLevelStockAwards(sisLevelAwards);
                break;
            default:
        }


        sisConfigRepository.save(sisConfig);

        resultModel.setCode(200);
        resultModel.setMessage("保存成功！");
        return resultModel;
    }

    /**
     * 保存等级开店商品
     * @param customerId            商户ID
     * @param openGoodsIdLevelId    等级对应商品的对象
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/saveLevelOpenGoodsConfig",method = RequestMethod.POST)
    @ResponseBody
    public ResultModel saveLevelOpenGoodsConfig(@CustomerId Long customerId,
                                                @RequestBody OpenGoodsIdLevelId openGoodsIdLevelId) throws Exception{
        ResultModel resultModel=new ResultModel();
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(customerId);
        if(sisConfig==null){
            resultModel.setCode(500);
            resultModel.setMessage("customer "+customerId+" have no SisConfig");
            return resultModel;
        }
        OpenGoodsIdLevelIds openGoodsIdLevelIds=sisConfig.getOpenGoodsIdlist();
        if(openGoodsIdLevelIds==null){
            openGoodsIdLevelIds=new OpenGoodsIdLevelIds();
        }
        openGoodsIdLevelIds.put(openGoodsIdLevelId.getLevelid(),openGoodsIdLevelId);
        sisConfig.setOpenGoodsIdlist(openGoodsIdLevelIds);
        sisConfigRepository.save(sisConfig);

        resultModel.setCode(200);
        resultModel.setMessage("保存成功！");
        return resultModel;
    }

    /**
     * 修改是否允许购买对应等级
     * @param customerId    商户ID
     * @param levelId       店铺等级ID
     * @param extraUpgrade  0：不能购买，1：可以购买
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/saveLevelExtraUpgrade",method = RequestMethod.POST)
    @ResponseBody
    public ResultModel saveLevelExtraUpgrade(@CustomerId Long customerId,
                                             @RequestParam(required = true) Long levelId,
                                             @RequestParam(required = true) Integer extraUpgrade) throws Exception{
        ResultModel resultModel=new ResultModel();
        SisLevel sisLevel=sisLevelRepository.findOne(levelId);
        if(sisLevel==null){
            resultModel.setCode(500);
            resultModel.setMessage("无法找到该等级");
            return resultModel;
        }
        sisLevel.setExtraUpgrade(extraUpgrade);
        sisLevelRepository.save(sisLevel);
        resultModel.setCode(200);
        resultModel.setMessage("保存成功！");
        return resultModel;
    }


    /**
     * 修改某商家是否能够升级店中店等级
     * @param customerId            商家ID
     * @param enableLevelUpgrade    是否能够升级
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/saveEnableLevelUpgrade",method = RequestMethod.POST)
    @ResponseBody
    public ResultModel saveEnableLevelUpgrade(@CustomerId Long customerId,
                                             @RequestParam(required = true) Boolean enableLevelUpgrade) throws Exception{
        ResultModel resultModel=new ResultModel();
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(customerId);
        if(sisConfig==null){
            resultModel.setCode(500);
            resultModel.setMessage("customer "+customerId+" have no SisConfig");
            return resultModel;
        }
        sisConfig.setEnableLevelUpgrade(enableLevelUpgrade);

        sisConfigRepository.save(sisConfig);

        resultModel.setCode(200);
        resultModel.setMessage("保存成功！");
        return resultModel;
    }
}
