package com.huotu.sis.controller.sis;

import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.Merchant;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.repository.MerchantRepository;
import com.huotu.huobanplus.sdk.mall.annotation.CustomerId;
import com.huotu.sis.common.StringHelper;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisGoodsRecommend;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.entity.VerificationCode;
import com.huotu.sis.model.sis.ResultModel;
import com.huotu.sis.model.sis.SimpleGoodsModel;
import com.huotu.sis.model.sis.SisSearchCodeModel;
import com.huotu.sis.model.sis.SisSearchModel;
import com.huotu.sis.model.sisweb.SisLevelModel;
import com.huotu.sis.model.sisweb.VerificationType;
import com.huotu.sis.repository.*;
import com.huotu.sis.service.CommonConfigsService;
import com.huotu.sis.service.SisService;
import com.huotu.sis.service.VerificationCodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by lgh on 2016/1/21.
 */
@Controller
@RequestMapping("/sis")
public class SisController {
    private static Log log = LogFactory.getLog(SisController.class);

    @Autowired
    Environment environment;

    @Autowired
    SisService sisService;

    @Autowired
    CommonConfigsService commonConfigService;

    @Autowired
    VerificationCodeService verificationCodeService;

    @Autowired
    VerificationCodeRepository verificationCodeRepository;

    @Autowired
    GoodRepository goodRepository;

    @Autowired
    SisGoodsRecommendRepository sisGoodsRecommendRepository;

    @Autowired
    MerchantRepository merchantRespository;

    @Autowired
    SisLevelRepository sisLevelRepository;

    @Autowired
    SisRepository sisRepository;



    /**
     * 店中店开店列表
     * @param sisSearchModel  查询条件
     * @param model             返回的参数
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showSisList",method = RequestMethod.GET)
    public String getYmrShareList(@CustomerId Long customerId, SisSearchModel sisSearchModel, Model model) throws Exception     {
        log.info(customerId+"into showSisList");
        if(environment.acceptsProfiles("develop")){
            customerId=4471L;
            model.addAttribute("dev",true);
        }

        if(customerId==null){
            throw new Exception("商户ID不存在");
        }
        sisSearchModel.setCustomerId(customerId);
        log.info(sisSearchModel.toString()+customerId);
        Page<Sis> sises=sisService.findSisList(sisSearchModel);
        if(sises!=null){
            for(Sis s:sises){
                User user=s.getUser();
                if(user!=null){
                    s.setImgPath(user.getWeixinImageUrl());
                }

            }

        }

        if(environment.acceptsProfiles("development")||environment.acceptsProfiles("test")||environment.acceptsProfiles("staging")){
            model.addAttribute("dev",true);
        }

        model.addAttribute("allSisList", sises);//文章列表model
        model.addAttribute("pageNo",sisSearchModel.getPageNoStr());//当前页数
        model.addAttribute("totalPages",sises.getTotalPages());//总页数
        model.addAttribute("totalRecords", sises.getTotalElements());//总记录数
        return "sis/openShopList";

    }

    /**
     * 查找验证码(7月14日，新版本弃用)
     * @param customerId            商户ID
     * @param sisSearchCodeModel    查询model
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showCodeList",method = RequestMethod.GET)
    public String showCodeList(@CustomerId Long customerId, SisSearchCodeModel sisSearchCodeModel, Model model) throws Exception{
        log.info(customerId+"into showCodeList");
        if(environment.acceptsProfiles("develop")){
            customerId=4471L;
        }
        if(customerId==null){
            throw new Exception("商户ID不存在");
        }
        log.info(sisSearchCodeModel.toString()+customerId);
        sisSearchCodeModel.setMerchantId(customerId);
        Page<VerificationCode> verificationCodes=verificationCodeService.findSisCodes(sisSearchCodeModel);
        model.addAttribute("allCodeList", verificationCodes);//文章列表model
        model.addAttribute("pageNo", sisSearchCodeModel.getPageNoStr());//当前页数
        model.addAttribute("totalPages", verificationCodes.getTotalPages());//总页数
        model.addAttribute("totalRecords", verificationCodes.getTotalElements());//总记录数
        return "sis/codeList";
    }

    /**
     * 更新验证码(7月14日，新版本弃用)
     * @param customerId    商户ID
     * @param mobile        手机号
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/setCode",method = RequestMethod.GET)
    @ResponseBody
    public ResultModel updateCode(@CustomerId Long customerId, String mobile)throws Exception{
        ResultModel resultModel=new ResultModel();
        if(environment.acceptsProfiles("develop")){
            customerId=4471L;
        }
        if(customerId==null){
            resultModel.setCode(500);
            resultModel.setMessage("商户ID不存在");
            return resultModel;
        }
        VerificationCode verificationCode = verificationCodeRepository.findByMobileAndTypeAndCustomerId(
                mobile, VerificationType.openBySelf,customerId);
        if (verificationCode == null) {
            verificationCode = new VerificationCode();
            verificationCode.setMobile(mobile);
            verificationCode.setType(VerificationType.openBySelf);
        }
        verificationCode.setSendTime(new Date());
        verificationCode.setCustomerId(customerId);
        verificationCode.setCode(StringHelper.RandomNum(new Random(), 4));
        verificationCode = verificationCodeRepository.save(verificationCode);

        resultModel.setCode(200);
        resultModel.setMessage(verificationCode.getCode());
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        resultModel.setData(sFormat.format(verificationCode.getSendTime()));
        return resultModel;
    }


    /**
     * 获取商家精选商品列表(需要优化)
     * @param customerId        商家ID
     * @param pageNoStr            分页
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showSisRecommendGoodsList",method = RequestMethod.GET)
    public String showSisRecommendGoodsList(@CustomerId Long customerId, Integer pageNoStr, Model model) throws Exception{
        if(environment.acceptsProfiles("develop")){
            customerId=4471L;
        }
        if(customerId==null){
            throw new Exception("商户ID不存在");
        }
        if(pageNoStr==null||pageNoStr<0){
            pageNoStr=0;
        }
        Page<Goods> goodses=goodRepository.findSisGoodsRecommendList(customerId,new PageRequest(pageNoStr,20));
        model.addAttribute("allGoodsList", goodses);//商品列表model
        model.addAttribute("pageNoStr", pageNoStr);//当前页数
        model.addAttribute("totalPages", goodses.getTotalPages());//总页数
        model.addAttribute("totalRecords", goodses.getTotalElements());//总记录数
        return "sis/sisRecommendGoodsList";
    }


    /**
     * 修改店主精选商品
     * @param customerId    商家ID
     * @param goodsId       商品ID
     * @param type          类型：0，删除店主精选商品，1添加店主精选商品,2.置顶(该功能需要优化)
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/modifySisRecommendGood",method = RequestMethod.GET)
    @ResponseBody
    public ResultModel modifySisRecommendGood(@CustomerId Long customerId, Long goodsId, Integer type)throws Exception{
        ResultModel resultModel=new ResultModel();
        if(environment.acceptsProfiles("develop")){
            customerId=4471L;
        }
        if(customerId==null){
            resultModel.setCode(500);
            resultModel.setMessage("商户ID不存在");
            return resultModel;
        }
        if(goodsId==null||type==null){
            resultModel.setCode(500);
            resultModel.setMessage("参数异常");
            return resultModel;
        }
        switch (type){
            case 0:
                sisGoodsRecommendRepository.deleteGoodsByGoodsId(goodsId);
                break;
            case 1:
                SisGoodsRecommend newSisGoods=sisGoodsRecommendRepository.findByGoodsId(goodsId);
                if(newSisGoods==null){
                    SisGoodsRecommend sisGoodsRecommend=new SisGoodsRecommend();
                    sisGoodsRecommend.setSortNo(50L);
                    sisGoodsRecommend.setCustomerId(customerId);
                    sisGoodsRecommend.setGoodsId(goodsId);
                    sisGoodsRecommendRepository.save(sisGoodsRecommend);

                }
                break;
            case 2:
                Long topSortNo=sisGoodsRecommendRepository.findMaxSortNoByCustomerId(customerId);
                SisGoodsRecommend top=sisGoodsRecommendRepository.findByGoodsId(goodsId);
                if(top==null){
                    resultModel.setCode(500);
                    resultModel.setMessage("找不到商品");
                    return resultModel;
                }
                top.setSortNo(topSortNo+1);
                sisGoodsRecommendRepository.save(top);
                break;
            default:
                break;
        }
        resultModel.setCode(200);
        resultModel.setMessage("OK");
        return resultModel;
    }


    /**
     * 获取商品列表(需要优化,有多个获取商品)
     * @param customerId
     * @param title
     * @param pageNo
     * @param pageSize
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showGoodsList",method = RequestMethod.GET)
    @ResponseBody
    public ResultModel showGoodsList(@CustomerId Long customerId, String title, Integer pageNo, Integer pageSize) throws Exception{
        ResultModel resultModel=new ResultModel();
        if(environment.acceptsProfiles("develop")){
            customerId=4471L;
        }
        if(customerId==null){
            resultModel.setCode(500);
            resultModel.setMessage("商户ID不存在");
            return resultModel;
        }
        Merchant merchant=merchantRespository.findOne(customerId);
        Page goodses=goodRepository.findByOwnerAndTitleLike(customerId,"%"+title+"%",new PageRequest(pageNo,pageSize));
        List<SimpleGoodsModel> goodsModels=new ArrayList<>();
        goodses.forEach(r->{
            Object[] obj = (Object[]) r;
            SimpleGoodsModel simpleGoodsModel=new SimpleGoodsModel();
            simpleGoodsModel.setId(obj[0]==null?0:Long.parseLong(obj[0].toString()));
            simpleGoodsModel.setTitle(obj[1]==null?"":obj[1].toString());
            simpleGoodsModel.setPrice(obj[2]==null?0:Double.parseDouble(obj[2].toString()));
            simpleGoodsModel.setStock(obj[3]==null?0:Integer.parseInt(obj[3].toString()));
            simpleGoodsModel.setIsRecommend(obj[4]==null?1:0);
            goodsModels.add(simpleGoodsModel);
        });
//        model.addAttribute("allCodeList", goodses);//文章列表model
//        model.addAttribute("pageNo", pageNo);//当前页数
//        model.addAttribute("totalPages", goodses.getTotalPages());//总页数
//        model.addAttribute("totalRecords", goodses.getTotalElements());//总记录数
        resultModel.setCode(200);
        resultModel.setData(goodsModels);
        resultModel.setMessage(String.valueOf(goodses.getTotalPages()));
        return resultModel;
    }


    /**
     *  获取商家的店铺等级列表
     * @param customerId    商家ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showLevelList",method = RequestMethod.GET)
    @ResponseBody
    public ResultModel showLevelList(@CustomerId Long customerId) throws Exception{
        ResultModel resultModel=new ResultModel();
        if(environment.acceptsProfiles("develop")){
            customerId=4471L;
        }
        if(customerId==null){
            resultModel.setCode(500);
            resultModel.setMessage("商户ID不存在");
            return resultModel;
        }
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
        resultModel.setCode(200);
        resultModel.setMessage("OK");
        resultModel.setData(sisLevelModels);
        return resultModel;
    }



    /**
     * 修改店铺等级
     * @param customerId    商户ID
     * @param sisId         店铺ID
     * @param sisLevelId    店铺等级ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/modifySisLevel", method = RequestMethod.POST)
    @ResponseBody
    public com.huotu.sis.model.sisweb.ResultModel modifySisLevel(@CustomerId Long customerId, Long sisLevelId, Long sisId) throws Exception {
        com.huotu.sis.model.sisweb.ResultModel resultModel=new com.huotu.sis.model.sisweb.ResultModel();

        if(customerId==null){
            resultModel.setCode(500);
            resultModel.setMessage("未找到商户ID");
            return resultModel;
        }
        if(sisLevelId==null||sisId==null){
            resultModel.setCode(500);
            resultModel.setMessage("参数错误");
            return resultModel;
        }
        Sis sis=sisRepository.findOne(sisId);
        SisLevel sisLevel=sisLevelRepository.findOne(sisLevelId);
        sis.setSisLevel(sisLevel);
        sisRepository.save(sis);
        resultModel.setCode(200);
        resultModel.setMessage("OK");
        return resultModel;
    }

    /**
     * 删除店铺等级(正式版本不显示此功能)
     * @param customerId    商户ID
     * @param sisId         店铺ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/delSis", method = RequestMethod.POST)
    @ResponseBody
    public com.huotu.sis.model.sisweb.ResultModel delSis(@CustomerId Long customerId, Long sisId) throws Exception {
        com.huotu.sis.model.sisweb.ResultModel resultModel=new com.huotu.sis.model.sisweb.ResultModel();

        if(customerId==null){
            resultModel.setCode(500);
            resultModel.setMessage("未找到商户ID");
            return resultModel;
        }
        if(sisId==null){
            resultModel.setCode(500);
            resultModel.setMessage("参数错误");
            return resultModel;
        }
        sisRepository.delete(sisId);
        resultModel.setCode(200);
        resultModel.setMessage("OK");
        return resultModel;
    }

}
