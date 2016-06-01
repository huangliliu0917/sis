package com.huotu.sis.controller;

import com.huotu.huobanplus.common.UserType;
import com.huotu.huobanplus.common.entity.*;
import com.huotu.huobanplus.common.entity.support.ProfitConfig;
import com.huotu.huobanplus.common.repository.GoodsRepository;
import com.huotu.huobanplus.common.repository.MerchantConfigRepository;
import com.huotu.huobanplus.common.repository.UserRepository;
import com.huotu.huobanplus.common.utils.DateUtil;
import com.huotu.huobanplus.sdk.mall.service.MallInfoService;
import com.huotu.sis.entity.*;
import com.huotu.sis.entity.support.ProfitUser;
import com.huotu.sis.model.ResultModel;
import com.huotu.sis.repository.*;
import com.huotu.sis.service.*;
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
import java.io.UnsupportedEncodingException;
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
    private MallInfoService mallInfoService;

    @Autowired
    private Environment environment;
    @Autowired
    private SisProfitService sisProfitService;

    /**
     * 升级用户店中店
     * <p>
     * <b>负责人：史利挺</b>
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/upgradeSisShop", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResultModel upgradeSisShop(HttpServletRequest request) throws Exception {
        log.info("upgradeSisShop");
        ResultModel resultModel = new ResultModel();
        //第一步:参数有效性判断
        if (!verifySign(request)) {
            resultModel.setCode(401);
            resultModel.setMessage("授权失败：签名未通过！");
            return resultModel;
        }

        String userId = request.getParameter("userid");
        if (StringUtils.isEmpty(userId)) {
            resultModel.setCode(403);
            resultModel.setMessage("参数错误：没有用户ID！");
            return resultModel;
        }
        User user = userRepository.findOne(Long.parseLong(userId));
        if (Objects.isNull(user)) {
            resultModel.setCode(403);
            resultModel.setMessage("参数错误：找不到用户！");
            return resultModel;
        }

        SisConfig sisConfig = sisConfigRepository.findByMerchantId(user.getMerchant().getId());
        if (sisConfig == null || sisConfig.getEnabled() == 0 || sisConfig.getOpenGoodsMode() == null || sisConfig.getOpenGoodsMode() == 0) {
            resultModel.setCode(403);
            resultModel.setMessage(userId + "商户无店中店配置不是升级的条件");
            return resultModel;
        }
        String orderId = request.getParameter("orderid");
        List<OrderItems> orderItems = sisOrderItemsRepository.getOrderItemsByOrderId(orderId);
        if (orderItems == null || orderItems.isEmpty()) {
            resultModel.setCode(403);
            resultModel.setMessage(orderId + "订单无效！");
            return resultModel;

        }
        Order order = sisOrderRepository.findOne(orderId);

        log.info("user:" + userId + ",upgradeSisShopOverOrderid:" + orderId);
        //第二步:升级
        boolean isUpgrade = sisLevelService.upgradeSisLevel(user, sisConfig, orderItems.get(0));

        //第三步:返利
        userService.countIntegral(user, order, IntegralType.upgrade);

        //第四步:返回结果
        if (isUpgrade) {
            resultModel.setCode(200);
            resultModel.setMessage("OK");
        } else {
            resultModel.setCode(500);
            resultModel.setMessage("Fail");
            log.info("user" + userId + "Upgrade failed");

        }
        return resultModel;
    }


    /**
     * 开店逻辑
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/openSisShop", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public ResultModel open(HttpServletRequest request) throws Exception {
        log.info("into openShop");
        ResultModel resultModel = new ResultModel();
        //签名验证
        if (!verifySign(request)) {
            resultModel.setCode(401);
            resultModel.setMessage("授权失败：签名未通过！");
            return resultModel;
        }

        //参数验证
        String userId = request.getParameter("userid");
        if (StringUtils.isEmpty(userId)) {
            resultModel.setCode(403);
            resultModel.setMessage("参数错误：没有用户ID！");
            return resultModel;
        }
        User user = userRepository.findOne(Long.parseLong(userId));
        if (Objects.isNull(user)) {
            resultModel.setCode(403);
            resultModel.setMessage("参数错误：找不到用户！");
            return resultModel;
        }
        Sis sis = sisRepository.findByUser(user);
        if (sis != null) {
            resultModel.setCode(500);
            resultModel.setMessage(userId + "店中店已经开启");
            return resultModel;
        }
        SisConfig sisConfig = sisConfigRepository.findByMerchantId(user.getMerchant().getId());

        if (sisConfig == null || sisConfig.getEnabled() == 0 ||
                sisConfig.getOpenGoodsMode() == null || sisConfig.getOpenGoodsMode() == 0 ||
                sisConfig.getOpenAwardMode() == null || sisConfig.getOpenAwardMode() == 0) {
            resultModel.setCode(403);
            resultModel.setMessage(userId + "商户无店中店配置或不是升级的条件");
            return resultModel;
        }

        String orderId = request.getParameter("orderid");
        String unionorderId = request.getParameter("unionorderid");
        log.info("userId:" + userId + "orderID=" + orderId + "into openShop");
        //开店
        userService.newOpen(user, orderId, sisConfig);
        log.info(user.getId() + "openShopOver");
        //开店奖计算
        userService.countOpenShopAward(user, orderId, unionorderId);
        log.info(user.getId() + "openCountOver");
        //合伙人送股
        userService.givePartnerStock(user, orderId, sisConfig);
        log.info(user.getId() + "songguOver");
        resultModel.setCode(200);
        resultModel.setMessage("OK");
        return resultModel;
    }


//    @ResponseStatus(HttpStatus.OK)
//    @RequestMapping(value = "/calculateShopRebate", method = {RequestMethod.POST,RequestMethod.GET})
//    @ResponseBody
//    public ResultModel calculateShopRebate(HttpServletRequest httpServletRequest) throws Exception {
//
//        ResultModel resultModel = new ResultModel();
//
//        if(!verifySign(httpServletRequest)){
//            resultModel.setCode(401);
//            resultModel.setMessage("授权失败：签名未通过！");
//            return resultModel;
//        }
//
//        String shopIdString=httpServletRequest.getParameter("shopid");
//        if(StringUtils.isEmpty(shopIdString)){
//            resultModel.setCode(402);
//            resultModel.setMessage("参数错误：没有shopId！");
//            return resultModel;
//        }
//
//        Long shopId=Long.parseLong(shopIdString);
//        String orderId=httpServletRequest.getParameter("orderid");
//        if(StringUtils.isEmpty(orderId)){
//            resultModel.setCode(403);
//            resultModel.setMessage("参数错误：没有orderId！");
//            return resultModel;
//        }
//        String unionOrderId=httpServletRequest.getParameter("unionorderid");
//        if(StringUtils.isEmpty(unionOrderId)){
//            resultModel.setCode(404);
//            resultModel.setMessage("参数错误：没有unionorderId！");
//            return resultModel;
//        }
//
//        User user=userRepository.findOne(shopId);//店主(会员)
//        if(Objects.isNull(user)){
//            resultModel.setCode(500);
//            resultModel.setMessage("未找到店主");
//            return resultModel;
//        }
//        Long shopLevelId=sisService.getSisLevelId(user);//店主店铺等级ID
//        if(Objects.isNull(shopLevelId)){
//            resultModel.setCode(500);
//            resultModel.setMessage("未找到店铺等级");
//            return resultModel;
//        }
//
//        Order order = sisOrderRepository.findOne(orderId);
//        if(Objects.isNull(order)){
//            resultModel.setCode(500);
//            resultModel.setMessage("未找到订单");
//            return resultModel;
//        }
//
//        List<OrderItems> orderItems = sisOrderItemsRepository.getOrderItemsByOrderId(orderId);
//        if(Objects.isNull(orderItems)){
//            resultModel.setCode(500);
//            resultModel.setMessage("未找到订单明细");
//            return resultModel;
//        }
//        SisLevel sisLevel = sisLevelRepository.findOne(shopLevelId);//
//        if(Objects.isNull(sisLevel)){
//            resultModel.setCode(500);
//            resultModel.setMessage("未找到店铺等级，无法返利");
//            return resultModel;
//        }
//
//        double rate=0;
//        rate = sisLevel.getRebateRate();
//
//
//
//        MerchantConfig merchantConfig =merchantConfigRepository.findByMerchant(user.getMerchant());
//        if(Objects.isNull(merchantConfig)){
//            resultModel.setCode(500);
//            resultModel.setMessage("未找到店铺配置信息");
//            return resultModel;
//        }
//        int exchangeRate=merchantConfig.getExchangeRate();//得到积分转余额的汇率
//
//        /**
//         * 转正时间的计算
//         */
//        Date date=new Date();
//        int positiveDay=merchantConfig.getPositiveDay();
//        Calendar ca=Calendar.getInstance();
//        ca.setTime(date);
//        ca.add(Calendar.DATE,positiveDay+1);
//        Date now=ca.getTime();//转正后的时间
//        Date now2 = DateUtil.makeStartDate(now);
//
//
//        double totalPrize = 0;
//        for (int i = 0; i < orderItems.size(); i++) {
//            double zhituiPrize =  orderItems.get(i).getZhituiPrize();
//            totalPrize += zhituiPrize;
//        }
//        totalPrize = (totalPrize * rate)/100;
//        UserTempIntegralHistory utih = new UserTempIntegralHistory();
//        User contriUser=userRepository.findOne((long)order.getUserId());//得到贡献人
//        UserType userType=contriUser.getUserType();
//        String desc="店主直推奖，订单号 :"+orderId;
//        int contributeUserType;
//        if(contriUser.getUserType()==UserType.normal){
//            contributeUserType=0;
//        }else{
//            contributeUserType=1;
//        }
//
//        int jifen=getIntegralRateByRate(totalPrize,exchangeRate);
//
//        utih.setCustomerId(user.getMerchant().getId());
//        utih.setIntegral(jifen);
//        utih.setUnionOrderId(unionOrderId);
//        utih.setUserId(user.getId());//受益人的id
//        utih.setStatus(0);
//        utih.setAddTime(new Date());
//        utih.setContributeBelongOne(contriUser.getBelongOne().intValue());
//        utih.setContributeUserType(contributeUserType);
//        utih.setDesc(desc);
//        utih.setPositiveFlag(1);
//        utih.setUserLevelId((long)user.getLevelId());
//        utih.setEstimatePostime(now2);
//        utih.setUserType(user.getUserType());
//        utih.setPositiveFlag(1);
//        utih.setType(0);
//        utih.setNewType(500);
//        utih.setOrder(order);
//        utih.setContributeDesc(null);
//        utih.setFlowIntegral(jifen);
//        utih.setContributeUserID((long)order.getUserId());
//        utih.setUserGroupId(0L);//进行21个设值.
//
//        utihRepository.save(utih);
//        resultModel.setCode(200);
//        jifen=user.getUserTempIntegral()+jifen;
//        user.setUserTempIntegral(jifen);
//        userRepository.save(user);
//        resultModel.setCode(200);
//        resultModel.setMessage("OK");
//        return resultModel;
//    }

    private int getIntegralRateByRate(double amount, int exchangeRate) {
        if (exchangeRate == 0) exchangeRate = 100;
        return (int) Math.rint(100 * amount / exchangeRate);
    }

    /**
     * 判断签名有效性
     *
     * @param request
     * @return
     */
    private boolean verifySign(HttpServletRequest request) throws UnsupportedEncodingException {
        if (!environment.acceptsProfiles("develop") && !environment.acceptsProfiles("development")) {
            String sign = request.getParameter("sign");
            if (sign == null || !sign.equals(securityService.getSign(request))) {
                return false;
            }
        }
        return true;
    }

//    @ResponseStatus(HttpStatus.OK)
//    @RequestMapping(value = "/calculateShopRebate", method = {RequestMethod.POST, RequestMethod.GET})
//    @ResponseBody
//    public ResultModel calculateShopRebate(HttpServletRequest httpServletRequest) throws Exception {
//
//        log.info("begin zhituijiang");
//
//        ResultModel resultModel = new ResultModel();
//
//        if (!verifySign(httpServletRequest)) {
//            resultModel.setCode(401);
//            resultModel.setMessage("授权失败：签名未通过！");
//            return resultModel;
//        }
//
//        String shopIdString = httpServletRequest.getParameter("shopid");
//        if (StringUtils.isEmpty(shopIdString)) {
//            resultModel.setCode(402);
//            resultModel.setMessage("参数错误：没有shopId！");
//            return resultModel;
//        }
//
//        Long shopId = Long.parseLong(shopIdString);
//        String orderId = httpServletRequest.getParameter("orderid");
//        if (StringUtils.isEmpty(orderId)) {
//            resultModel.setCode(403);
//            resultModel.setMessage("参数错误：没有orderId！");
//            return resultModel;
//        }
//        String unionOrderId = httpServletRequest.getParameter("unionorderid");
//        if (StringUtils.isEmpty(unionOrderId)) {
//            resultModel.setCode(404);
//            resultModel.setMessage("参数错误：没有unionorderId！");
//            return resultModel;
//        }
//
//        User user = userRepository.findOne(shopId);//店主(会员)
//        if (Objects.isNull(user)) {
//            resultModel.setCode(500);
//            resultModel.setMessage("未找到店主");
//            return resultModel;
//        }
//        log.info("user weixin name：" + user.getWxNickName());
//        Long shopLevelId = sisService.getSisLevelId(user);//店主店铺等级ID
//        if (Objects.isNull(shopLevelId)) {
//            resultModel.setCode(500);
//            resultModel.setMessage("未找到店铺等级");
//            return resultModel;
//        }
//
//        Order order = sisOrderRepository.findOne(orderId);
//        if (Objects.isNull(order)) {
//            resultModel.setCode(500);
//            resultModel.setMessage("未找到订单");
//            return resultModel;
//        }
//
//        List<OrderItems> orderItems = sisOrderItemsRepository.getOrderItemsByOrderId(orderId);
//        if (Objects.isNull(orderItems)) {
//            resultModel.setCode(500);
//            resultModel.setMessage("未找到订单明细");
//            return resultModel;
//        }
//        SisLevel sisLevel = sisLevelRepository.findOne(shopLevelId);//
//        if (Objects.isNull(sisLevel)) {
//            resultModel.setCode(500);
//            resultModel.setMessage("未找到店铺等级，无法返利");
//            return resultModel;
//        }
//
////        double rate;
////        rate = sisLevel.getRebateRate();
//        Long customerId = user.getMerchant().getId();
//
//
//        MerchantConfig merchantConfig = merchantConfigRepository.findByMerchant(user.getMerchant());
//        if (Objects.isNull(merchantConfig)) {
//            resultModel.setCode(500);
//            resultModel.setMessage("未找到店铺配置信息");
//            return resultModel;
//        }
//        int exchangeRate = merchantConfig.getExchangeRate();//得到积分转余额的汇率
//
//        /**
//         * 转正时间的计算
//         */
//        Date date = new Date();
//        int positiveDay = merchantConfig.getPositiveDay();
//        Calendar ca = Calendar.getInstance();
//        ca.setTime(date);
//        ca.add(Calendar.DATE, positiveDay + 1);
//        Date now = ca.getTime();//转正后的时间
//        Date now2 = DateUtil.makeStartDate(now);
//
//
//        double totalPrize = 0;
//        for (int i = 0; i < orderItems.size(); i++) {
//            double prize = orderItems.get(i).getZhituiPrize();
////            Goods goods = goodsRepository.findOne((long)orderItems.get(i).getGoodsId());
////            double prize = goods.getPrice();
//            totalPrize += prize;
//        }
//        if (totalPrize == 0) {
//            resultModel.setCode(500);
//            resultModel.setMessage("商品售价为0，无法获利");
//            return resultModel;
//        }
//        User contriUser = userRepository.findOne((long) order.getUserId());//得到贡献人
//        String desc = "dianzhu order :" + orderId;
//        int contributeUserType;
//        if (contriUser.getUserType() == UserType.normal) {
//            contributeUserType = 0;
//        } else {
//            contributeUserType = 1;
//        }
//
//        Integer userLevelStatus = userService.getTotalUserType((long) user.getLevelId());
//        log.info("dianzhu level：" + userLevelStatus);
//        //总代一小伙伴
//        if (userLevelStatus == 1) {
//            List<SISProfit> profits = sisProfitService.findAllByUserLevelId((long) user.getLevelId(),
//                    customerId, sisLevel.getId());
//            //自己
//            SISProfit ownerProfit = profits.stream().filter(item -> item.getProfitUser().equals(ProfitUser.owner)).findAny().get();
////            SISProfit oneBelongProfit = profits.stream().filter(item -> item.getProfitUser().equals(ProfitUser.oneBelong)).findAny().get();
//            //总代二
////            Long value = userService.getValueByKey(SysConfigConstant.Total_Generation_TwoId);
////            SISProfit twoProfit = profits.stream().filter(item -> item.getProfitUser().equals(ProfitUser.oneBelong)).findAny().get();
//            //自己的直推积分
//            int ownerIntegral = getIntegralRateByRate(totalPrize * ownerProfit.getProfit() / 100, exchangeRate);
//            //保存临时积分
//            saveHistory(customerId, ownerIntegral, unionOrderId, user, contriUser, contributeUserType, desc, now2, order);
//            if (Objects.nonNull(user.getBelongOne())) {
//                //上级的积分
//                User belongOneUser = userRepository.findOne(user.getBelongOne());
//                if (Objects.nonNull(belongOneUser)) {
//                    Integer belongOneLevelStatus = userService.getTotalUserType((long) belongOneUser.getLevelId());
//                    List<SISProfit> oneProfits;
//                    //如果上级是总代一
//                    if (belongOneLevelStatus == 1) {
//                        Long oneShopLevelId = sisService.getSisLevelId(belongOneUser);//店主店铺等级ID
//                        oneProfits = sisProfitService.findAllByUserLevelId((long) belongOneUser.getLevelId(),
//                                customerId, oneShopLevelId);
//                        SISProfit oneBelongProfit;
//                        Sis sis = sisRepository.findByUser(belongOneUser);
//                        //专卖店 todo 待改
//                        if (sis.getSisLevel().getId().equals(""))
//                            oneBelongProfit = oneProfits.stream().filter(item ->
//                                    item.getProfitUser().equals(ProfitUser.oneBelong)).findAny().get();
//                        else //旗舰店
//                            oneBelongProfit = oneProfits.stream().filter(item ->
//                                    item.getProfitUser().equals(ProfitUser.oneBelongFlagship)).findAny().get();
//
//
//                        int belongOneIntegral = getIntegralRateByRate(totalPrize * oneBelongProfit.getProfit() / 100, exchangeRate);
//                        log.info("one level integral：" + belongOneIntegral);
//                        saveHistory(customerId, belongOneIntegral, unionOrderId, belongOneUser, contriUser,
//                                contributeUserType, desc, now2, order);
//                        //如果上上级有总代二
//                        if (Objects.nonNull(belongOneUser.getBelongOne())) {
//                            User belongTwoUser = userService.findTotalGenerationTwoByUser(belongOneUser);
//                            if (Objects.nonNull(belongTwoUser)) {
//                                List<SISProfit> twoProfits = sisProfitService.findAllByUserLevelId((long) belongTwoUser.getLevelId(),
//                                        customerId, null);
//                                SISProfit twoBelongProfit = twoProfits.stream().filter(item ->
//                                        item.getProfitUser().equals(ProfitUser.oneBelong)).findAny().get();
//                                int belongTwoIntegral = getIntegralRateByRate(totalPrize
//                                        * twoBelongProfit.getProfit() / 100, exchangeRate);
//                                log.info("two level integral：" + belongOneIntegral);
//                                saveHistory(customerId, belongTwoIntegral, unionOrderId, belongTwoUser, contriUser,
//                                        contributeUserType, desc, now2, order);
//                            }
//                        }
//                    } else if (belongOneLevelStatus == 2) {
//                        //自己就是总代二
//                        oneProfits = sisProfitService.findAllByUserLevelId((long) belongOneUser.getLevelId(),
//                                customerId, null);
//                        SISProfit oneBelongProfit = oneProfits.stream().filter(item ->
//                                item.getProfitUser().equals(ProfitUser.oneBelong)).findAny().get();
//                        int belongOneIntegral = getIntegralRateByRate(totalPrize * oneBelongProfit.getProfit() / 100, exchangeRate);
//                        log.info("one level integral：" + belongOneIntegral);
//                        saveHistory(customerId, belongOneIntegral, unionOrderId, belongOneUser, contriUser,
//                                contributeUserType, desc, now2, order);
//                    }
//
//
//                }
//
//            }
//            resultModel.setCode(200);
//            resultModel.setMessage("OK");
//            return resultModel;
//        } else if (userLevelStatus == 2) {
//            //总代二小伙伴
//            List<SISProfit> profits = sisProfitService.findAllByUserLevelId((long) user.getLevelId(),
//                    customerId, null);
//            SISProfit ownerProfit = profits.stream().filter(item -> item.getProfitUser().equals(ProfitUser.owner)).findAny().get();
////            SISProfit oneBelongProfit = profits.stream().filter(item -> item.getProfitUser().equals(ProfitUser.oneBelong)).findAny().get();
//            //自己的直推积分
//            int ownerIntegral = getIntegralRateByRate(totalPrize * ownerProfit.getProfit() / 100, exchangeRate);
//            //保存临时积分
//            saveHistory(customerId, ownerIntegral, unionOrderId, user, contriUser, contributeUserType, desc, now2, order);
//            resultModel.setCode(200);
//            resultModel.setMessage("OK");
//            return resultModel;
//        } else {
//            resultModel.setCode(500);
//            resultModel.setMessage("当前用户不属于可获利的等级");
//            return resultModel;
//        }
//    }


    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/calculateShopRebate", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResultModel calculateShopRebate(HttpServletRequest httpServletRequest) throws Exception {

        log.info("begin zhituijiang");

        ResultModel resultModel = new ResultModel();

        if (!verifySign(httpServletRequest)) {
            resultModel.setCode(401);
            resultModel.setMessage("授权失败：签名未通过！");
            return resultModel;
        }

        String shopIdString = httpServletRequest.getParameter("shopid");
        if (StringUtils.isEmpty(shopIdString)) {
            resultModel.setCode(402);
            resultModel.setMessage("参数错误：没有shopId！");
            return resultModel;
        }

        Long shopId = Long.parseLong(shopIdString);
        String orderId = httpServletRequest.getParameter("orderid");
        if (StringUtils.isEmpty(orderId)) {
            resultModel.setCode(403);
            resultModel.setMessage("参数错误：没有orderId！");
            return resultModel;
        }
        String unionOrderId = httpServletRequest.getParameter("unionorderid");
        if (StringUtils.isEmpty(unionOrderId)) {
            resultModel.setCode(404);
            resultModel.setMessage("参数错误：没有unionorderId！");
            return resultModel;
        }

        User user = userRepository.findOne(shopId);//店主(会员)
        if (Objects.isNull(user)) {
            resultModel.setCode(500);
            resultModel.setMessage("未找到店主");
            return resultModel;
        }
        log.info("user weixin name：" + user.getWxNickName());
        Long shopLevelId = sisService.getSisLevelId(user);//店主店铺等级ID
        if (Objects.isNull(shopLevelId)) {
            resultModel.setCode(500);
            resultModel.setMessage("未找到店铺等级");
            return resultModel;
        }

        Order order = sisOrderRepository.findOne(orderId);
        if (Objects.isNull(order)) {
            resultModel.setCode(500);
            resultModel.setMessage("未找到订单");
            return resultModel;
        }

        List<OrderItems> orderItems = sisOrderItemsRepository.getOrderItemsByOrderId(orderId);
        if (Objects.isNull(orderItems)) {
            resultModel.setCode(500);
            resultModel.setMessage("未找到订单明细");
            return resultModel;
        }
        SisLevel sisLevel = sisLevelRepository.findOne(shopLevelId);//
        if (Objects.isNull(sisLevel)) {
            resultModel.setCode(500);
            resultModel.setMessage("未找到店铺等级，无法返利");
            return resultModel;
        }

//        double rate;
//        rate = sisLevel.getRebateRate();
        Long customerId = user.getMerchant().getId();


        MerchantConfig merchantConfig = merchantConfigRepository.findByMerchant(user.getMerchant());
        if (Objects.isNull(merchantConfig)) {
            resultModel.setCode(500);
            resultModel.setMessage("未找到店铺配置信息");
            return resultModel;
        }
        int exchangeRate = merchantConfig.getExchangeRate();//得到积分转余额的汇率

        /**
         * 转正时间的计算
         */
        Date date = new Date();
        int positiveDay = merchantConfig.getPositiveDay();
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.DATE, positiveDay + 1);
        Date now = ca.getTime();//转正后的时间
        Date now2 = DateUtil.makeStartDate(now);


//        if (totalPrize == 0) {
//            resultModel.setCode(500);
//            resultModel.setMessage("商品售价为0，无法获利");
//            return resultModel;
//        }
        User contriUser = userRepository.findOne((long) order.getUserId());//得到贡献人
        String desc = "dianzhu order :" + orderId;
        int contributeUserType;
        if (contriUser.getUserType() == UserType.normal) {
            contributeUserType = 0;
        } else {
            contributeUserType = 1;
        }

        //一大堆定义
        //上级
        User belongOneUser = null;
        //上上级
        User belongTwoUser = null;
        int ownerIntegralAll = 0;
        int belongOneIntegralAll = 0;
        int belongTwoIntegralAll = 0;

        Integer userLevelStatus = userService.getTotalUserType((long) user.getLevelId());

        Long ownerShopLevelId = sisService.getSisLevelId(user);//店主店铺等级ID
        //查询自己是专卖店还是旗舰店
        Integer ownerSisLevelStatus = userService.getTotalGeneraltionType(ownerShopLevelId);

        double totalPrize = 0;
        for (OrderItems item : orderItems) {
            double prize = item.getZhituiPrize() * item.getAmount();
            if (null == item.getProfitConfigs()) {
                totalPrize += prize;
            } else {
                log.debug("商品个性化:" + item.getGoodsId());
                List<ProfitConfig> profitConfigs = item.getProfitConfigs();
                if (null != profitConfigs && profitConfigs.size() > 0) {
                    //总代一
                    if (userLevelStatus == 1) {

                        double ownerProfitPrice;
                        //1表示专卖店，2表示旗舰店
                        if (ownerSisLevelStatus == 2) {
                            ownerProfitPrice = profitConfigs.stream().filter(config -> config.getProfitKey().equals("zong1_qj_self")).findAny().get().getProfitValue();
                        } else {
                            ownerProfitPrice = profitConfigs.stream().filter(config -> config.getProfitKey().equals("zong1_zm_self")).findAny().get().getProfitValue();
                        }
                        int ownerIntegral = getIntegralRateByRate(prize * ownerProfitPrice / 100, exchangeRate);
                        ownerIntegralAll += ownerIntegral;
                        saveHistory(customerId, ownerIntegral, unionOrderId, user, contriUser, contributeUserType, desc, now2, order, 500);
//                        mallInfoService.pushMessage(orderId,order.getTitle(),)
                        if (Objects.nonNull(user.getBelongOne())) {
                            if (null == belongOneUser)
                                belongOneUser = userRepository.findOne(user.getBelongOne());
                            if (Objects.nonNull(belongOneUser)) {
                                Integer belongOneLevelStatus = userService.getTotalUserType((long) belongOneUser.getLevelId());
                                //如果上级是总代一
                                if (belongOneLevelStatus == 1) {
//                                    Long oneShopLevelId = sisService.getSisLevelId(belongOneUser);//店主店铺等级ID
                                    //查询是专卖店还是旗舰店
//                                    Integer oneSisLevelStatus = userService.getTotalGeneraltionType(oneShopLevelId);
                                    double oneBelongProfitPrice;
                                    //专卖店 todo 待改
                                    if (ownerSisLevelStatus == 1)
                                        oneBelongProfitPrice = profitConfigs.stream().filter(config -> config.getProfitKey().equals("zong1_zm_zm")).findAny().get().getProfitValue();
                                    else //旗舰店
                                        oneBelongProfitPrice = profitConfigs.stream().filter(config -> config.getProfitKey().equals("zong1_zm_qj")).findAny().get().getProfitValue();


                                    int belongOneIntegral = getIntegralRateByRate(prize * oneBelongProfitPrice / 100, exchangeRate);
                                    belongOneIntegralAll += belongOneIntegral;
                                    log.info("one level integral：" + belongOneIntegral);
                                    saveHistory(customerId, belongOneIntegral, unionOrderId, belongOneUser, contriUser,
                                            contributeUserType, desc, now2, order, 501);
                                    //如果上上级有总代二
                                    if (Objects.nonNull(belongOneUser.getBelongOne())) {
                                        if (null == belongTwoUser)
                                            belongTwoUser = userService.findTotalGenerationTwoByUser(belongOneUser);
                                        if (Objects.nonNull(belongTwoUser)) {
//                                            Long twoShopLevelId = sisService.getSisLevelId(belongTwoUser);//店主店铺等级ID
                                            //查询是专卖店还是旗舰店
//                                            Integer twoSisLevelStatus = userService.getTotalGeneraltionType(twoShopLevelId);
                                            double twoBelongProfitPrice;
                                            //专卖店 todo 待改
                                            if (ownerSisLevelStatus == 1)
                                                twoBelongProfitPrice = profitConfigs.stream().filter(config -> config.getProfitKey().equals("zong2_zm")).findAny().get().getProfitValue();
                                            else //旗舰店
                                                twoBelongProfitPrice = profitConfigs.stream().filter(config -> config.getProfitKey().equals("zong2_qj")).findAny().get().getProfitValue();
                                            int belongTwoIntegral = getIntegralRateByRate(prize
                                                    * twoBelongProfitPrice / 100, exchangeRate);
                                            belongTwoIntegralAll += belongTwoIntegral;
                                            saveHistory(customerId, belongTwoIntegral, unionOrderId, belongTwoUser, contriUser,
                                                    contributeUserType, desc, now2, order, 502);
                                        }
                                    }
                                } else if (belongOneLevelStatus == 2) {
                                    //上级就是总代二

//                                    Long oneShopLevelId = sisService.getSisLevelId(belongOneUser);//店主店铺等级ID

                                    //查询是专卖店还是旗舰店
//                                    Integer oneSisLevelStatus = userService.getTotalGeneraltionType(oneShopLevelId);
                                    double oneBelongProfitPrice;
                                    //专卖店 todo 待改
                                    if (ownerSisLevelStatus == 1)
                                        oneBelongProfitPrice = profitConfigs.stream().filter(config -> config.getProfitKey().equals("zong2_zm")).findAny().get().getProfitValue();
                                    else {//旗舰店
                                        oneBelongProfitPrice = profitConfigs.stream().filter(config -> config.getProfitKey().equals("zong2_qj")).findAny().get().getProfitValue();
                                    }
                                    int belongOneIntegral = getIntegralRateByRate(prize * oneBelongProfitPrice / 100, exchangeRate);
                                    belongOneIntegralAll += belongOneIntegral;
                                    saveHistory(customerId, belongOneIntegral, unionOrderId, belongOneUser, contriUser,
                                            contributeUserType, desc, now2, order, 501);
                                }
                            }
                        }

                    } else if (userLevelStatus == 2) {
                        //总代二小伙伴
                        double ownerProfitPrice = profitConfigs.stream().filter(config -> config.getProfitKey().equals("zong2_self")).findAny().get().getProfitValue();
                        //自己的直推积分
                        int ownerIntegral = getIntegralRateByRate(prize * ownerProfitPrice / 100, exchangeRate);
                        ownerIntegralAll += ownerIntegral;
                        //保存临时积分
                        saveHistory(customerId, ownerIntegral, unionOrderId, user, contriUser, contributeUserType, desc, now2, order, 500);
                    }
                }
            }
        }

        if (totalPrize > 0) {
            //总代一小伙伴
            if (userLevelStatus == 1) {
                List<SISProfit> profits = sisProfitService.findAllByUserLevelId((long) user.getLevelId(),
                        customerId, sisLevel.getId());
                //自己
                SISProfit ownerProfit = profits.stream().filter(item -> item.getProfitUser().equals(ProfitUser.owner)).findAny().get();
                //自己的直推积分
                int ownerIntegral = getIntegralRateByRate(totalPrize * ownerProfit.getProfit() / 100, exchangeRate);
                ownerIntegralAll += ownerIntegral;
                //保存临时积分
                saveHistory(customerId, ownerIntegral, unionOrderId, user, contriUser, contributeUserType, desc, now2, order, 500);
                if (Objects.nonNull(user.getBelongOne())) {
                    //上级的积分
                    if (null == belongOneUser)
                        belongOneUser = userRepository.findOne(user.getBelongOne());
                    if (Objects.nonNull(belongOneUser)) {
                        Integer belongOneLevelStatus = userService.getTotalUserType((long) belongOneUser.getLevelId());
                        List<SISProfit> oneProfits;
                        //如果上级是总代一
                        if (belongOneLevelStatus == 1) {
                            Long oneShopLevelId = sisService.getSisLevelId(belongOneUser);//店主店铺等级ID
                            oneProfits = sisProfitService.findAllByUserLevelId((long) belongOneUser.getLevelId(),
                                    customerId, oneShopLevelId);
                            SISProfit oneBelongProfit;
                            //查询是专卖店还是旗舰店
//                            Integer oneSisLevelStatus = userService.getTotalGeneraltionType(oneShopLevelId);
                            //专卖店 todo 待改
                            if (ownerSisLevelStatus == 1)
                                oneBelongProfit = oneProfits.stream().filter(item ->
                                        item.getProfitUser().equals(ProfitUser.oneBelong)).findAny().get();
                            else //旗舰店
                                oneBelongProfit = oneProfits.stream().filter(item ->
                                        item.getProfitUser().equals(ProfitUser.oneBelongFlagship)).findAny().get();


                            int belongOneIntegral = getIntegralRateByRate(totalPrize * oneBelongProfit.getProfit() / 100, exchangeRate);
                            belongOneIntegralAll += belongOneIntegral;
                            log.info("one level integral：" + belongOneIntegral);
                            saveHistory(customerId, belongOneIntegral, unionOrderId, belongOneUser, contriUser,
                                    contributeUserType, desc, now2, order, 501);
                            //如果上上级有总代二
                            if (Objects.nonNull(belongOneUser.getBelongOne())) {
                                if (null == belongTwoUser)
                                    belongTwoUser = userService.findTotalGenerationTwoByUser(belongOneUser);
                                if (Objects.nonNull(belongTwoUser)) {
//                                    Long twoShopLevelId = sisService.getSisLevelId(belongTwoUser);//店主店铺等级ID
                                    List<SISProfit> twoProfits = sisProfitService.findAllByUserLevelId((long) belongTwoUser.getLevelId(),
                                            customerId, null);
                                    SISProfit twoBelongProfit;
                                    //查询是专卖店还是旗舰店
//                                    Integer twoSisLevelStatus = userService.getTotalGeneraltionType(twoShopLevelId);
                                    //专卖店 todo 待改
                                    if (ownerSisLevelStatus == 1)
                                        twoBelongProfit = twoProfits.stream().filter(item ->
                                                item.getProfitUser().equals(ProfitUser.oneBelong)).findAny().get();
                                    else //旗舰店
                                        twoBelongProfit = twoProfits.stream().filter(item ->
                                                item.getProfitUser().equals(ProfitUser.oneBelongFlagship)).findAny().get();
                                    int belongTwoIntegral = getIntegralRateByRate(totalPrize
                                            * twoBelongProfit.getProfit() / 100, exchangeRate);
                                    belongTwoIntegralAll += belongTwoIntegral;
                                    log.info("two level integral：" + belongOneIntegral);
                                    saveHistory(customerId, belongTwoIntegral, unionOrderId, belongTwoUser, contriUser,
                                            contributeUserType, desc, now2, order, 502);
                                }
                            }
                        } else if (belongOneLevelStatus == 2) {
                            //上级就是总代二
                            oneProfits = sisProfitService.findAllByUserLevelId((long) belongOneUser.getLevelId(),
                                    customerId, null);

//                            Long oneShopLevelId = sisService.getSisLevelId(belongOneUser);//店主店铺等级ID
                            SISProfit oneBelongProfit;
                            //查询是专卖店还是旗舰店
//                            Integer oneSisLevelStatus = userService.getTotalGeneraltionType(oneShopLevelId);
                            //专卖店 todo 待改
                            if (ownerSisLevelStatus == 1)
                                oneBelongProfit = oneProfits.stream().filter(item ->
                                        item.getProfitUser().equals(ProfitUser.oneBelong)).findAny().get();
                            else //旗舰店
                                oneBelongProfit = oneProfits.stream().filter(item ->
                                        item.getProfitUser().equals(ProfitUser.oneBelongFlagship)).findAny().get();
                            int belongOneIntegral = getIntegralRateByRate(totalPrize * oneBelongProfit.getProfit() / 100, exchangeRate);
                            belongOneIntegralAll += belongOneIntegral;
                            log.info("one level integral：" + belongOneIntegral);
                            saveHistory(customerId, belongOneIntegral, unionOrderId, belongOneUser, contriUser,
                                    contributeUserType, desc, now2, order, 501);
                        }


                    }

                }
            } else if (userLevelStatus == 2) {
                //总代二小伙伴
                List<SISProfit> profits = sisProfitService.findAllByUserLevelId((long) user.getLevelId(),
                        customerId, null);
                SISProfit ownerProfit = profits.stream().filter(item -> item.getProfitUser().equals(ProfitUser.owner)).findAny().get();
//            SISProfit oneBelongProfit = profits.stream().filter(item -> item.getProfitUser().equals(ProfitUser.oneBelong)).findAny().get();
                //自己的直推积分
                int ownerIntegral = getIntegralRateByRate(totalPrize * ownerProfit.getProfit() / 100, exchangeRate);
                ownerIntegralAll += ownerIntegral;
                //保存临时积分
                saveHistory(customerId, ownerIntegral, unionOrderId, user, contriUser, contributeUserType, desc, now2, order, 500);
            } else {
                resultModel.setCode(500);
                resultModel.setMessage("当前用户不属于可获利的等级");
                return resultModel;
            }
        }
        String nickname = contriUser.getLoginName() + "(" + contriUser.getWxNickName() + ")";
        log.debug("dianzhujifen:"+ownerIntegralAll+" shangjijifen:"+belongOneIntegralAll+" shangshangjijifen:"+belongTwoIntegralAll);
        if (ownerIntegralAll > 0) {
            String ownerStatus = mallInfoService.pushMessage(order.getId(), order.getTitle(), order.getPrice(), order.getTime(), order.getPayTime()
                    , "自己", nickname, ownerIntegralAll, customerId, user.getId());
            if(!"OK".equals(ownerStatus)){
                log.info("integral for owner error:"+ownerStatus);
            }
        }
        if (belongOneIntegralAll > 0) {
            String ownerStatus = mallInfoService.pushMessage(order.getId(), order.getTitle(), order.getPrice(), order.getTime(), order.getPayTime()
                    , "一级", nickname, belongOneIntegralAll, customerId, belongOneUser.getId());
            if(!"OK".equals(ownerStatus)){
                log.info("integral for belongOne error:"+ownerStatus);
            }
        }
        if (belongTwoIntegralAll > 0) {
            String ownerStatus = mallInfoService.pushMessage(order.getId(), order.getTitle(), order.getPrice(), order.getTime(), order.getPayTime()
                    , "二级", nickname, belongTwoIntegralAll, customerId, belongTwoUser.getId());
            if(!"OK".equals(ownerStatus)){
                log.info("integral for belongTwo error:"+ownerStatus);
            }
        }
        resultModel.setCode(200);
        resultModel.setMessage("该订单商品的直推返利全部个性化");
        return resultModel;
    }

    /**
     * @param customerId
     * @param integral
     * @param unionOrderId
     * @param user
     * @param contriUser
     * @param contributeUserType
     * @param desc
     * @param estimatePostime
     * @param order
     * @param newType            500:店主，501：上级，502：上上级
     */
    private void saveHistory(Long customerId, int integral, String unionOrderId, User user, User contriUser,
                             int contributeUserType, String desc, Date estimatePostime, Order order, Integer newType) {
        UserTempIntegralHistory utih = new UserTempIntegralHistory();
        utih.setCustomerId(customerId);
        utih.setIntegral(integral);
        utih.setUnionOrderId(unionOrderId);
        utih.setUserId(user.getId());//受益人的id
        utih.setStatus(0);
        utih.setAddTime(new Date());
        utih.setContributeBelongOne(contriUser.getBelongOne().intValue());
        utih.setContributeUserType(contributeUserType);
        utih.setDesc(desc);
        utih.setPositiveFlag(1);
        utih.setUserLevelId((long) user.getLevelId());
        utih.setEstimatePostime(estimatePostime);
        utih.setUserType(user.getUserType());
        utih.setPositiveFlag(1);
        utih.setType(newType);
        utih.setNewType(newType);
        utih.setOrder(order);
        utih.setContributeDesc(null);
        utih.setFlowIntegral(integral);
        utih.setContributeUserID((long) order.getUserId());
        utih.setUserGroupId(0L);//进行21个设值.
        utihRepository.save(utih);

        integral = user.getUserTempIntegral() + integral;
        user.setUserTempIntegral(integral);
        userRepository.save(user);
        log.info("zhituijaing success");
    }
}
