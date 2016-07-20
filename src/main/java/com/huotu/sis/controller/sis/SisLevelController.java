package com.huotu.sis.controller.sis;

/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

import com.huotu.huobanplus.sdk.mall.annotation.CustomerId;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.support.OpenGoodsIdLevelId;
import com.huotu.sis.entity.support.OpenGoodsIdLevelIds;
import com.huotu.sis.entity.support.SisLevelOpenAward;
import com.huotu.sis.entity.support.SisLevelAwards;
import com.huotu.sis.exception.SisException;
import com.huotu.sis.model.sis.ResultModel;
import com.huotu.sis.repository.SisConfigRepository;
import com.huotu.sis.service.SisLevelService;
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

    /**
     * 显示返利配置页面
     * @param customerId
     * @param levelId
     * @param type      返利类型 0：开店奖，1：直推奖，2：梦想股
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping("/showLevelOpenAwardConfig")
    public String showLevelOpenAwardConfig(@CustomerId Long customerId,
                                           @RequestParam(required = true) Long levelId,
                                           @RequestParam(required = true) Integer type,Model model) throws Exception{
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(customerId);
        if(sisConfig==null){
            throw new SisException("customer "+customerId+" have no SisConfig");
        }
        //获取开店配置
        SisLevelAwards sisLevelAwards=sisConfig.getSisLevelOpenAwards();
        SisLevelOpenAward sisLevelAward=new SisLevelOpenAward();
        if(sisLevelAwards!=null){
            sisLevelAward=sisLevelAwards.get(levelId);
        }
        //初始化sisLevelAward
        if(sisLevelAward==null||sisLevelAward.getCfg()==null){
            sisLevelAward=sisLevelService.initSisLevelOpenAward(customerId,levelId);
        }
        model.addAttribute("sislevels",sisLevelService.getSimpleSisLevelModel(customerId));
        model.addAttribute("cfgs",sisLevelAward.getCfg());
        model.addAttribute("levelId",sisLevelAward.getBuySisLvId());
        return "sis/newOpenAwardConfig";


    }

//    @RequestMapping("/showStockConfig")
//    public String showStockConfig(@CustomerId Long customerId,
//                                           @RequestParam(required = true) Long levelId, Model model) throws Exception{
//        SisConfig sisConfig=sisConfigRepository.findByMerchantId(customerId);
//        if(sisConfig==null){
//            throw new SisException("customer "+customerId+" have no SisConfig");
//        }
//        SisLevelAwards sisLevelAwards=sisConfig.getSisLevelOpenAwards();
//        SisLevelOpenAward sisLevelAward=new SisLevelOpenAward();
//        if(sisLevelAwards!=null){
//            sisLevelAward=sisLevelAwards.get(levelId);
//        }
//        //初始化sisLevelAward
//        if(sisLevelAward==null||sisLevelAward.getCfg()==null){
//            sisLevelAward=sisLevelService.initSisLevelOpenAward(customerId,levelId);
//        }
//        model.addAttribute("sislevels",sisLevelService.getSimpleSisLevelModel(customerId));
//        model.addAttribute("cfgs",sisLevelAward.getCfg());
//        model.addAttribute("levelId",sisLevelAward.getBuySisLvId());
//        return "sis/newOpenAwardConfig";
//
//
//    }

    @RequestMapping(value = "/saveLevelOpenAwardConfig",method = RequestMethod.POST)
    @ResponseBody
    public ResultModel saveLevelOpenAwardConfig(@CustomerId Long customerId,
                                                @RequestBody SisLevelOpenAward sisLevelAward) throws Exception{
        ResultModel resultModel=new ResultModel();
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(customerId);
        if(sisConfig==null){
            resultModel.setCode(500);
            resultModel.setMessage("customer "+customerId+" have no SisConfig");
            return resultModel;
        }
        SisLevelAwards sisLevelAwards=sisConfig.getSisLevelOpenAwards();
        if(sisLevelAwards==null){
            sisLevelAwards=new SisLevelAwards();
        }
        sisLevelAwards.put(sisLevelAward.getBuySisLvId(),sisLevelAward);
        sisConfig.setSisLevelOpenAwards(sisLevelAwards);
        sisConfigRepository.save(sisConfig);

        resultModel.setCode(200);
        resultModel.setMessage("保存成功！");
        return resultModel;
    }

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
}
