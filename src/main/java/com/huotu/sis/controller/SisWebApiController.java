package com.huotu.sis.controller;

import com.huotu.huobanplus.common.UserType;
import com.huotu.huobanplus.common.entity.*;
import com.huotu.huobanplus.common.repository.GoodsRepository;
import com.huotu.huobanplus.common.repository.MerchantConfigRepository;
import com.huotu.huobanplus.common.repository.UserRepository;
import com.huotu.huobanplus.common.utils.DateUtil;
import com.huotu.sis.common.SysConfigConstant;
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
    private GoodsRepository goodsRepository;

    @Autowired
    private Environment environment;
    @Autowired
    private SisProfitRepository sisProfitRepository;
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


        double totalPrize = 0;
        for (int i = 0; i < orderItems.size(); i++) {
            double prize = orderItems.get(i).getZhituiPrize();
//            Goods goods = goodsRepository.findOne((long)orderItems.get(i).getGoodsId());
//            double prize = goods.getPrice();
            totalPrize += prize;
        }
        if (totalPrize == 0) {
            resultModel.setCode(500);
            resultModel.setMessage("商品售价为0，无法获利");
            return resultModel;
        }
        User contriUser = userRepository.findOne((long) order.getUserId());//得到贡献人
        String desc = "dianzhu order :" + orderId;
        int contributeUserType;
        if (contriUser.getUserType() == UserType.normal) {
            contributeUserType = 0;
        } else {
            contributeUserType = 1;
        }

        Integer userLevelStatus = userService.getTotalUserType((long) user.getLevelId());
        log.info("dianzhu level：" + userLevelStatus);
        //总代一小伙伴
        if (userLevelStatus == 1) {
            List<SISProfit> profits = sisProfitService.findAllByUserLevelId((long) user.getLevelId(),
                    customerId, sisLevel.getId());
            //自己
            SISProfit ownerProfit = profits.stream().filter(item -> item.getProfitUser().equals(ProfitUser.owner)).findAny().get();
//            SISProfit oneBelongProfit = profits.stream().filter(item -> item.getProfitUser().equals(ProfitUser.oneBelong)).findAny().get();
            //总代二
//            Long value = userService.getValueByKey(SysConfigConstant.Total_Generation_TwoId);
//            SISProfit twoProfit = profits.stream().filter(item -> item.getProfitUser().equals(ProfitUser.oneBelong)).findAny().get();
            //自己的直推积分
            int ownerIntegral = getIntegralRateByRate(totalPrize * ownerProfit.getProfit() / 100, exchangeRate);
            //保存临时积分
            saveHistory(customerId, ownerIntegral, unionOrderId, user, contriUser, contributeUserType, desc, now2, order);
            if (Objects.nonNull(user.getBelongOne())) {
                //上级的积分
                User belongOneUser = userRepository.findOne(user.getBelongOne());
                if (Objects.nonNull(belongOneUser)) {
                    Integer belongOneLevelStatus = userService.getTotalUserType((long) belongOneUser.getLevelId());
                    List<SISProfit> oneProfits;
                    //如果上级是总代一
                    if (belongOneLevelStatus == 1) {
                        Long oneShopLevelId = sisService.getSisLevelId(belongOneUser);//店主店铺等级ID
                        oneProfits = sisProfitService.findAllByUserLevelId((long) belongOneUser.getLevelId(),
                                customerId, oneShopLevelId);
                        SISProfit oneBelongProfit = oneProfits.stream().filter(item ->
                                item.getProfitUser().equals(ProfitUser.oneBelong)).findAny().get();
                        int belongOneIntegral = getIntegralRateByRate(totalPrize * oneBelongProfit.getProfit() / 100, exchangeRate);
                        log.info("one level integral：" + belongOneIntegral);
                        saveHistory(customerId, belongOneIntegral, unionOrderId, belongOneUser, contriUser,
                                contributeUserType, desc, now2, order);
                        //如果上上级有总代二
                        if (Objects.nonNull(belongOneUser.getBelongOne())) {
                            User belongTwoUser = userService.findTotalGenerationTwoByUser(belongOneUser);
                            if (Objects.nonNull(belongTwoUser)) {
                                List<SISProfit> twoProfits = sisProfitService.findAllByUserLevelId((long) belongTwoUser.getLevelId(),
                                        customerId, null);
                                SISProfit twoBelongProfit = twoProfits.stream().filter(item ->
                                        item.getProfitUser().equals(ProfitUser.oneBelong)).findAny().get();
                                int belongTwoIntegral = getIntegralRateByRate(totalPrize
                                        * twoBelongProfit.getProfit() / 100, exchangeRate);
                                log.info("two level integral：" + belongOneIntegral);
                                saveHistory(customerId, belongTwoIntegral, unionOrderId, belongTwoUser, contriUser,
                                        contributeUserType, desc, now2, order);
                            }
                        }
                    } else if (belongOneLevelStatus == 2) {
                        //自己就是总代二
                        oneProfits = sisProfitService.findAllByUserLevelId((long) belongOneUser.getLevelId(),
                                customerId, null);
                        SISProfit oneBelongProfit = oneProfits.stream().filter(item ->
                                item.getProfitUser().equals(ProfitUser.oneBelong)).findAny().get();
                        int belongOneIntegral = getIntegralRateByRate(totalPrize * oneBelongProfit.getProfit() / 100, exchangeRate);
                        log.info("one level integral：" + belongOneIntegral);
                        saveHistory(customerId, belongOneIntegral, unionOrderId, belongOneUser, contriUser,
                                contributeUserType, desc, now2, order);
                    }



                }

            }
            resultModel.setCode(200);
            resultModel.setMessage("OK");
            return resultModel;
        } else if (userLevelStatus == 2) {
            //总代二小伙伴
            List<SISProfit> profits = sisProfitService.findAllByUserLevelId((long) user.getLevelId(),
                    customerId, null);
            SISProfit ownerProfit = profits.stream().filter(item -> item.getProfitUser().equals(ProfitUser.owner)).findAny().get();
//            SISProfit oneBelongProfit = profits.stream().filter(item -> item.getProfitUser().equals(ProfitUser.oneBelong)).findAny().get();
            //自己的直推积分
            int ownerIntegral = getIntegralRateByRate(totalPrize * ownerProfit.getProfit() / 100, exchangeRate);
            //保存临时积分
            saveHistory(customerId, ownerIntegral, unionOrderId, user, contriUser, contributeUserType, desc, now2, order);
//            if (Objects.nonNull(user.getBelongOne())) {
//                //上级的积分
//                User belongOneUser = userRepository.findOne(user.getBelongOne());
//                if (Objects.nonNull(belongOneUser)) {
//                    int belongOneIntegral = getIntegralRateByRate(totalPrize * ownerProfit.getProfit() / 100
//                            * oneBelongProfit.getProfit() / 100, exchangeRate);
//                    saveHistory(customerId, belongOneIntegral, unionOrderId, belongOneUser, contriUser,
//                            contributeUserType, desc, now2, order);
//                    log.info("上级积分：" + belongOneIntegral);
//                }
//            }
            resultModel.setCode(200);
            resultModel.setMessage("OK");
            return resultModel;
        } else {
            resultModel.setCode(500);
            resultModel.setMessage("当前用户不属于可获利的等级");
            return resultModel;
        }
    }

    private void saveHistory(Long customerId, int integral, String unionOrderId, User user, User contriUser,
                             int contributeUserType, String desc, Date estimatePostime, Order order) {
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
        utih.setType(0);
        utih.setNewType(500);
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
