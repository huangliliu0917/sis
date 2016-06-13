package com.huotu.sis.controller.sisweb;

import com.huotu.huobanplus.common.UserType;
import com.huotu.huobanplus.common.entity.*;
import com.huotu.huobanplus.common.repository.MerchantConfigRepository;
import com.huotu.huobanplus.common.repository.UserRepository;
import com.huotu.huobanplus.common.utils.DateUtil;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.model.sisweb.ResultModel;
import com.huotu.sis.repository.*;
import com.huotu.sis.service.SecurityService;
import com.huotu.sis.service.SisLevelService;
import com.huotu.sis.service.SisService;
import com.huotu.sis.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by slt on 2016/2/18.
 */
@Controller
@RequestMapping("/sisapi")
public class SisWebApiController {
    private static Log log = LogFactory.getLog(SisWebApiController.class);
    @Autowired
    SisInviteRepository sisInviteRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SisOrderRepository sisOrderRepository;

    @Autowired
    private SisOrderItemsRepository sisOrderItemsRepository;

    @Autowired
    private SisLevelRepository sisLevelRepository;

    @Autowired
    private UTIHRepository utihRepository;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserService userService;

    @Autowired
    private MerchantConfigRepository merchantConfigRepository;

    @Autowired
    private SisService sisService;

    @Autowired
    private SisRepository sisRepository;

    @Autowired
    private SisConfigRepository sisConfigRepository;

    @Autowired
    private SisLevelService sisLevelService;

    @Autowired
    private Environment environment;

    /**
     * 升级用户店中店
     *
     * <b>负责人：史利挺</b>
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/upgradeSisShop",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResultModel upgradeSisShop(HttpServletRequest request) throws Exception {
        log.info("upgradeSisShop");
        ResultModel resultModel=new ResultModel();
        //第一步:参数有效性判断
        if(!environment.acceptsProfiles("develop")&&!environment.acceptsProfiles("development")){
            String sign=request.getParameter("sign");
            if (sign == null || !sign.equals(securityService.getSign(request))) {
                resultModel.setCode(401);
                resultModel.setMessage("授权失败：签名未通过！");
                return resultModel;
            }

        }
        String userId=request.getParameter("userid");
        if(StringUtils.isEmpty(userId)){
            resultModel.setCode(403);
            resultModel.setMessage("参数错误：没有用户ID！");
            return resultModel;
        }
        User user = userRepository.findOne(Long.parseLong(userId));
        if(Objects.isNull(user)){
            resultModel.setCode(403);
            resultModel.setMessage("参数错误：找不到用户！");
            return resultModel;
        }

        SisConfig sisConfig=sisConfigRepository.findByMerchantId(user.getMerchant().getId());
        if(sisConfig==null||sisConfig.getEnabled()==0||sisConfig.getOpenGoodsMode()==null||sisConfig.getOpenGoodsMode()==0){
            resultModel.setCode(403);
            resultModel.setMessage(userId+"商户无店中店配置不是升级的条件");
            return resultModel;
        }
        String orderId=request.getParameter("orderid");
        List<OrderItems> orderItems=sisOrderItemsRepository.getOrderItemsByOrderId(orderId);
        if(orderItems==null||orderItems.isEmpty()){
            resultModel.setCode(403);
            resultModel.setMessage(orderId+"订单无效！");
            return resultModel;

        }

        log.info("user:"+userId+",upgradeSisShopOverOrderid:"+orderId);
        //第二步:升级
        boolean isUpgrade=sisLevelService.upgradeSisLevel(user,sisConfig,orderItems.get(0));

        //第三步:返回结果
        if(isUpgrade){
            resultModel.setCode(200);
            resultModel.setMessage("OK");
        }else {
            resultModel.setCode(500);
            resultModel.setMessage("Fail");
            log.info("user"+userId+"Upgrade failed");

        }
        return resultModel;
    }


    /**
     * 开店操作，具体步骤：
     * 1.开启店中店
     * 2.用户返利
     * 3.合伙人送股
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/openSisShop",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public ResultModel open(HttpServletRequest request) throws Exception {
        log.info("into openShop");
        ResultModel resultModel=new ResultModel();

        if(!environment.acceptsProfiles("develop")&&!environment.acceptsProfiles("development")){
            //签名验证
            String sign=request.getParameter("sign");
            if (sign == null || !sign.equals(securityService.getSign(request))) {
                resultModel.setCode(401);
                resultModel.setMessage("授权失败：签名未通过！");
                return resultModel;
            }

        }

        //参数验证
        String userId=request.getParameter("userid");
        if(StringUtils.isEmpty(userId)){
            resultModel.setCode(403);
            resultModel.setMessage("参数错误：没有用户ID！");
            return resultModel;
        }
        User user = userRepository.findOne(Long.parseLong(userId));
        if(Objects.isNull(user)){
            resultModel.setCode(403);
            resultModel.setMessage("参数错误：找不到用户！");
            return resultModel;
        }
        Sis sis = sisRepository.findByUser(user);
        if(sis!=null){
            resultModel.setCode(500);
            resultModel.setMessage(userId+"店中店已经开启");
            return resultModel;
        }
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(user.getMerchant().getId());
        if(sisConfig==null){
            resultModel.setCode(500);
            resultModel.setMessage(userId+"商户没有店中店配置信息");
            return resultModel;
        }

        String orderId=request.getParameter("orderid");
        String unionorderId=request.getParameter("unionorderid");
        log.info("userId:"+userId+"orderID="+orderId+"into openShop");
        //开店
        userService.newOpen(user,orderId,sisConfig);
        log.info(user.getId()+"openShopOver");
        //开店奖计算
        userService.countOpenShopAward(user, orderId, unionorderId,sisConfig);
        log.info(user.getId() + "openCountOver");
        //合伙人送股
        userService.givePartnerStock(user, orderId,sisConfig);
        log.info(user.getId() + "songguOver");
        resultModel.setCode(200);
        resultModel.setMessage("OK");
        return resultModel;
    }



//    /**
//     * 计算直推奖 straight push prize
//     * @param httpServletRequest
//     * @return
//     * @throws Exception
//     */
//    @ResponseStatus(HttpStatus.OK)
//    @RequestMapping(value = "/calShopRebate", method = {RequestMethod.POST,RequestMethod.GET})
//    @ResponseBody
//    public ResultModel calShopRebate(HttpServletRequest httpServletRequest) throws Exception {
//        ResultModel resultModel=new ResultModel();
//        //安全有效性验证
//        if(!environment.acceptsProfiles("develop")&&!environment.acceptsProfiles("development")){
//            //签名验证
//            String sign=httpServletRequest.getParameter("sign");
//            if (sign == null || !sign.equals(securityService.getSign(httpServletRequest))) {
//                resultModel.setCode(401);
//                resultModel.setMessage("授权失败：签名未通过！");
//                return resultModel;
//            }
//        }
//        //参数验证
//        return resultModel;
//    }




    /**
     * 计算直推奖
     * @param httpServletRequest
     * @return
     * @throws Exception
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/calculateShopRebate", method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public ResultModel calculateShopRebate(HttpServletRequest httpServletRequest) throws Exception {

        ResultModel resultModel = new ResultModel();
        String sign=httpServletRequest.getParameter("sign");
        if (sign == null || !sign.equals(securityService.getSign(httpServletRequest))) {
            resultModel.setCode(401);
            resultModel.setMessage("授权失败：签名未通过！");
            return resultModel;
        }
        String shopIdString=httpServletRequest.getParameter("shopid");
        if(StringUtils.isEmpty(shopIdString)){
            resultModel.setCode(402);
            resultModel.setMessage("参数错误：没有shopId！");
            return resultModel;
        }

        Long shopId=Long.parseLong(shopIdString);
        String orderId=httpServletRequest.getParameter("orderid");
        if(StringUtils.isEmpty(orderId)){
            resultModel.setCode(403);
            resultModel.setMessage("参数错误：没有orderId！");
            return resultModel;
        }
        String unionOrderId=httpServletRequest.getParameter("unionorderid");
        if(StringUtils.isEmpty(unionOrderId)){
            resultModel.setCode(404);
            resultModel.setMessage("参数错误：没有unionorderId！");
            return resultModel;
        }

        User user=userRepository.findOne(shopId);//店主(会员)
        if(Objects.isNull(user)){
            resultModel.setCode(500);
            resultModel.setMessage("未找到店主");
            return resultModel;
        }
        Long shopLevelId=sisService.getSisLevelId(user);//店主店铺等级ID
        if(shopLevelId==0){
            resultModel.setCode(500);
            resultModel.setMessage("未找到店铺等级");
            return resultModel;
        }

        Order order = sisOrderRepository.findOne(orderId);
        if(Objects.isNull(order)){
            resultModel.setCode(500);
            resultModel.setMessage("未找到订单");
            return resultModel;
        }

        List<OrderItems> orderItems = sisOrderItemsRepository.getOrderItemsByOrderId(orderId);
        if(Objects.isNull(orderItems)){
            resultModel.setCode(500);
            resultModel.setMessage("未找到订单明细");
            return resultModel;
        }
        SisLevel sisLevel = sisLevelRepository.findOne(shopLevelId);//
        if(Objects.isNull(sisLevel)){
            resultModel.setCode(500);
            resultModel.setMessage("未找到店铺等级，无法返利");
            return resultModel;
        }


        MerchantConfig merchantConfig =merchantConfigRepository.findByMerchant(user.getMerchant());
        if(Objects.isNull(merchantConfig)){
            resultModel.setCode(500);
            resultModel.setMessage("未找到店铺配置信息");
            return resultModel;
        }

        sisService.calculatePushAward(user,order,unionOrderId);

        double rate=sisLevel.getRebateRate();

        int exchangeRate=merchantConfig.getExchangeRate();//得到积分转余额的汇率

        /**
         * 转正时间的计算
         */
        Date date=new Date();
        int positiveDay=merchantConfig.getPositiveDay();
        Calendar ca=Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.DATE,positiveDay+1);
        Date now=ca.getTime();//转正后的时间
        Date now2 = DateUtil.makeStartDate(now);


        double totalPrize = 0;
        for (int i = 0; i < orderItems.size(); i++) {
            double zhituiPrize =  orderItems.get(i).getZhituiPrize();
            totalPrize += zhituiPrize*orderItems.get(i).getAmount();
        }
        totalPrize = (totalPrize * rate)/100;
        UserTempIntegralHistory utih = new UserTempIntegralHistory();
        User contriUser=userRepository.findOne((long)order.getUserId());//得到贡献人
        UserType userType=contriUser.getUserType();
        String desc="店主直推奖，订单号 :"+orderId;
        int contributeUserType;
        if(contriUser.getUserType()==UserType.normal){
            contributeUserType=0;
        }else{
            contributeUserType=1;
        }

        int jifen=getIntegralRateByRate(totalPrize,exchangeRate);

        utih.setCustomerId(user.getMerchant().getId());
        utih.setIntegral(jifen);
        utih.setUnionOrderId(unionOrderId);
        utih.setUserId(user.getId());//受益人的id
        utih.setStatus(0);
        utih.setAddTime(new Date());
        utih.setContributeBelongOne(contriUser.getBelongOne().intValue());
        utih.setContributeUserType(contributeUserType);
        utih.setDesc(desc);
        utih.setPositiveFlag(1);
        utih.setUserLevelId((long)user.getLevelId());
        utih.setEstimatePostime(now2);
        utih.setUserType(user.getUserType());
        utih.setType(0);
        utih.setNewType(500);
        utih.setOrder(order);
        utih.setContributeDesc(null);
        utih.setFlowIntegral(jifen);
        utih.setContributeUserID((long)order.getUserId());
        utih.setUserGroupId(0L);//进行21个设值.
        utihRepository.save(utih);

        resultModel.setCode(200);
        jifen=user.getUserTempIntegral()+jifen;
        user.setUserTempIntegral(jifen);
        userRepository.save(user);


        resultModel.setCode(200);
        resultModel.setMessage("OK");
        return resultModel;

//        resultModel.setCode(200);
//        resultModel.setMessage("OK");
//        return resultModel;
    }

    private int getIntegralRateByRate(double amount,int exchangeRate){
        if (exchangeRate == 0) exchangeRate = 100;
        return  (int)Math.rint(100 * amount / exchangeRate);
    }


}
