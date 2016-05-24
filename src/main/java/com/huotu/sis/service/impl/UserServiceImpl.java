package com.huotu.sis.service.impl;

import com.huotu.common.base.CookieHelper;
import com.huotu.common.base.RSAHelper;
import com.huotu.huobanplus.common.entity.*;
import com.huotu.huobanplus.common.repository.*;
import com.huotu.huobanplus.smartui.entity.TemplatePage;
import com.huotu.huobanplus.smartui.entity.support.Scope;
import com.huotu.huobanplus.smartui.repository.TemplatePageRepository;
import com.huotu.sis.common.SysConfigConstant;
import com.huotu.sis.entity.*;
import com.huotu.sis.entity.support.OpenGoodsIdLevelId;
import com.huotu.sis.entity.support.OpenGoodsIdLevelIds;
import com.huotu.sis.repository.*;
import com.huotu.sis.service.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by lgh on 2015/12/30.
 */
@Service
public class UserServiceImpl implements UserService {

    private static Log log = LogFactory.getLog(UserServiceImpl.class);
    private String userKey = "userId";
    private String loginApp="loginApp";
    private String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAK8Cd7gS6dOz3ALnICrlLOiGWv5RHgiQFvSsKMBudp59UDrsknSsaZsdBagrhMdtlKxYI1JzD2iJrGGBjPexvtGXVFrZjkLXZCdmeiM0L41m7VvkeI4ASD/4T3qxSjhMCRAVvJ0vC/sPffKR71In0hyUWMrFXCPR10zGUmcU9TwVAgMBAAECgYAeP1/vuZ0eUOTCv62onEmBus75S43UTwsYqLS2ZaEszV3TgVXiwnXSMFbs9PCTA1aB3w3jzy0nlTvs8lYp7VecWjG+rqayIZk2HtdKwNoEroqOPLgvDUTwxCC30CByZL4yb95XhNFBpd4p7cJLlPgf8M58WT7ttS3UquJDhYPYgQJBAPD8/HH07yMS17VfO6KwM7OCsnwUdrH3mGtK3ac86Z5xhelK4ikiKetu+1QFSeUOLm4Uv4K67c6lko+yPvmjqzUCQQC56VP0QhexQj4sylGtGSjqYGftRkkhbg6zHLURR0RMzTw+jXP8J6R0xTlIiBKJyK2xgAXuuqNUlyQVEJwCSolhAkEAmQ+F43c3P+ai3Q7MmMsjO1vCs25n6SciRts5JxRYKYtfC0rFlGyfhWpq9PWa9oHoWYCSFp1Vl4+wI9aJixM6FQJAA43vefsNgukWUTrpBts1Sg3fzsyKN2ZoR4pj99mZ97Hw1e1Ua1zCqyzeJIHdgN7iW0NsWZ0d5E8jdHel0/Fi4QJAJhb7Mt8pC5UCJNTJc1JQyzTUiZAvGr4EeTiAi0MCkPH85pOEr6ChMH4qI/51nTTqskoMJtd/xHxMDbEeXkrnAg==";
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCvAne4EunTs9wC5yAq5Szohlr+UR4IkBb0rCjAbnaefVA67JJ0rGmbHQWoK4THbZSsWCNScw9oiaxhgYz3sb7Rl1Ra2Y5C12QnZnojNC+NZu1b5HiOAEg/+E96sUo4TAkQFbydLwv7D33yke9SJ9IclFjKxVwj0ddMxlJnFPU8FQIDAQAB";

    @Autowired
    private Environment env;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SisRepository sisRepository;
    @Autowired
    private TemplatePageRepository templatePageRepository;
    @Autowired
    private SisLevelRepository sisLevelRepository;

    @Autowired
    private MallAdvanceLogsService mallAdvanceLogsService;

    @Autowired
    private MallCptMembersRepository mallCptMembersRepository;
    @Autowired
    private MallCptCfgRepository mallCptCfgRepository;
    @Autowired
    private MallCptStockLogService mallCptStockLogService;

    @Autowired
    private SisInviteRepository sisInviteRepository;

    @Autowired
    private SisOrderItemsRepository sisOrderItemsRepository;

    @Autowired
    private SisOpenAwardAssignRepository sisOpenAwardAssignRepository;

    @Autowired
    private UserFormalIntegralRepository userFormalIntegralRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @Autowired
    private IndirectPushFlowRepository indirectPushFlowRepository;

    @Autowired
    private SisOpenAwardLogService sisOpenAwardLogService;

    @Override
    public Long getUserId(HttpServletRequest request) {
        if (env.acceptsProfiles("development")){
//            userRepository.findAll();
//            return 97278L;//146 4471商户 王明
            return 120034L;
        }
        else {
            String encrypt = CookieHelper.get(request, userKey);
            try {
                Long userId = Long.parseLong(RSAHelper.decrypt(encrypt, privateKey));
                if (userId > 0) return userId;
                return null;
            } catch (Exception ex) {
                return null;
            }
        }
    }

    @Override
    public void setUserId(Long userId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (userId > 0) {
            String encrypt = RSAHelper.encrypt(userId.toString(), publicKey);
            CookieHelper.set(response, userKey, encrypt, request.getServerName(), 60 * 60 * 24 * 365);
//            CookieHelper.set(response, userKey, userId.toString(), request.getServerName(), 60 * 60 * 24 * 365);
        }

    }

    @Override
    public void setLoginType(HttpServletRequest request,HttpServletResponse response,String loginType) throws Exception {
        String encrypt = RSAHelper.encrypt(loginType, publicKey);
        CookieHelper.set(response, loginApp,encrypt, request.getServerName(), 60 * 60 * 24 * 365);

    }

    @Override
    public String getLoginType(HttpServletRequest request) throws Exception {
//        if (env.acceptsProfiles("development")){
//            return "1";
//        }
        String encrypt = CookieHelper.get(request, loginApp);
        if(encrypt==null){
            return "0";
        }
        try {
            String type = RSAHelper.decrypt(encrypt, privateKey);
            if("1".equals(type)){
                return type;
            }
            return "0";
        } catch (Exception ex) {
            return "0";
        }
    }

    @Override
    public void open(Long userId) {
        User user = userRepository.findOne(userId);
        Sis sis = sisRepository.findByUser(user);
        if (sis == null) {
            List<TemplatePage> templatePage = templatePageRepository.findByScopeAndEnabled(Scope.sis, true);
            Sis sis1 = new Sis();
            sis1.setTitle("我的小店");
            sis1.setImgPath("");
            sis1.setShareDesc("分享描述");
            sis1.setShareTitle("分享标题");
            sis1.setStatus(true);
            if (templatePage.size() > 0) {
                sis1.setTemplateId(templatePage.get(0).getId());
            }
            sis1.setUser(user);
            sis1.setDescription("我的小店描述");
            sisRepository.save(sis1);
        }
    }

    @Override
    public void newOpen(User user,String orderId,SisConfig sisConfig) throws Exception {
        Sis sis = sisRepository.findByUser(user);
        List<TemplatePage> templatePage = templatePageRepository.findByScopeAndEnabled(Scope.sis, true);
        SisInviteLog sisInviteLog=sisInviteRepository.findFirstByAcceptIdOrderByAcceptTimeDesc(user.getId());
        //开店用户等级设置
        SisLevel sisLevel = sisLevelRepository.findByMerchantIdSys(user.getMerchant().getId());
        if(sisConfig.getOpenGoodsMode()==1&&sisConfig.getOpenMode()==1){
            OrderItems orderItems=sisOrderItemsRepository.getOrderItemsByOrderId(orderId).get(0);
            OpenGoodsIdLevelIds openGoodsIdLevelIds=sisConfig.getOpenGoodsIdlist();
            //根据订单号找到该用户购买的开店等级
                for(OpenGoodsIdLevelId ol:openGoodsIdLevelIds.values()){
                    if(orderItems.getGoodsId()!=null){
                        if(Long.valueOf(orderItems.getGoodsId()).equals(ol.getGoodsid())){
                            sisLevel=sisLevelRepository.findOne(ol.getLevelid());
                            break;

                        }
                    }
                }
        }
        if (sis == null) {
            sis = new Sis();
            sis.setImgPath("");
            sis.setTitle("我的小店");
            sis.setShareDesc("分享描述");
            sis.setShareTitle("分享标题");
            sis.setOpenTime(new Date());
            sis.setStatus(true);
            if (templatePage.size() > 0) {
                sis.setTemplateId(templatePage.get(0).getId());
            }
            sis.setUser(user);
            sis.setDescription("我的小店描述");

            sis.setSisLevel(sisLevel);
            //新增字段
            sis.setCustomerId(user.getMerchant().getId());
            if(sisInviteLog!=null){
                if(!StringUtils.isEmpty(sisInviteLog.getRealName())){
                    sis.setTitle(sisInviteLog.getRealName()+"的小店");
                }
                sis.setRealName(sisInviteLog.getRealName());
                sis.setMobile(sisInviteLog.getMobile());
            }
        }
        sisRepository.save(sis);
    }

    @Override
    public void countOpenShopAward(User user, String orderId, String unionOrderId) throws Exception {
        Order order=orderRepository.findOne(orderId);
        //返利计算
        countIntegral(user,order,IntegralType.open);
        //间推,插流水
        saveIndirectPushFlow(user,order);
    }

    @Override
    public void countIntegral(User user, Order order,IntegralType integralType) throws Exception {
        User belongOne=userRepository.findOne(user.getBelongOne());
        //没有上级，无法返利
        if(belongOne==null){
            log.info("user:"+user.getId()+"have no belongOneId");
            return;
        }
        Sis belongOneSis=sisRepository.findByUser(belongOne);
        if(belongOneSis==null){
            log.info("user"+user.getId()+"belongOne have no sisShop");
            return;
        }
        //获取自己店铺
        Sis ownSis=sisRepository.findByUser(user);
        if(ownSis==null){
            log.info("user"+user.getId()+"won have no sisShop");
            return;
        }

        SisOpenAwardAssign sisOpenAwardAssign=sisOpenAwardAssignRepository.findByLevel_IdAndGuideLevel_IdAndUserLevel(
                (long)belongOne.getLevelId(),
                belongOneSis.getSisLevel(),
                ownSis.getSisLevel()).get(0);
        if(sisOpenAwardAssign==null){
            //无法找到升级返利配置信息
            log.info("user"+user.getId()+"Upgrade rebate configuration information cannot be found");
            return;
        }
        //增加返利积分流水
        saveFormalIntegral(user,belongOne,sisOpenAwardAssign.getIntegral(),order,integralType);

        //店中店返利流水
        String contributionName=user.getWxNickName()==null?"":user.getWxNickName();
        String remark="一级会员"+contributionName+integralType.getName();
        sisOpenAwardLogService.saveSisOpenAwardLog(user.getMerchant().getId(),user.getBelongOne(),user.getId()
        ,sisOpenAwardAssign.getIntegral().doubleValue(),remark,1,order.getId());

        //用户冗余字段修改积分
        addUserIntegral(user,sisOpenAwardAssign.getIntegral());

    }

    @Override
    public UserFormalIntegral saveFormalIntegral(User user, User beloneOne,Integer value, Order order,IntegralType integralType) throws Exception {
        if(value<=0){
            log.info("user"+beloneOne.getId()+"add integral is 0");
            return null;
        }
        UserFormalIntegral userFormalIntegral=new UserFormalIntegral();
        userFormalIntegral.setMerchant(beloneOne.getMerchant());
        userFormalIntegral.setOrder(order);
        userFormalIntegral.setUserLevelId((long)beloneOne.getLevelId());
        userFormalIntegral.setScore(value);
        userFormalIntegral.setUser(beloneOne);
        userFormalIntegral.setTime(new Date());
        userFormalIntegral.setStatus(integralType.getIndex());
        userFormalIntegral.setDesc("1级会员"+user.getWxNickName()+"贡献"+ integralType.getName());
        userFormalIntegralRepository.save(userFormalIntegral);
        return userFormalIntegral;



    }

    @Override
    public void addUserIntegral(User user, Integer value) throws Exception {
        if(value<=0){
            log.info("user"+user.getId()+"add integral is 0");
            return;
        }
        user.setUserIntegral(user.getUserIntegral()+value);
        userRepository.save(user);
    }

    @Override
    public void rebateOpenShop(User earningsUser, User contributeUser,Double money, String orderId, String unionOrderId, Integer srcType) throws Exception {
        if (money <= 0) {
            log.info("user" + earningsUser.getId() + "rebate is 0");
            return;
        }
        BigDecimal balance = new BigDecimal(earningsUser.getUserBalance());
        balance = balance.add(new BigDecimal(money));
        earningsUser.setUserBalance(balance.doubleValue());
        userRepository.save(earningsUser);
        //插入一条开店奖流水
        mallAdvanceLogsService.saveMallAdvanceLogs(earningsUser,contributeUser, money, orderId, unionOrderId, srcType);
    }


    @Override
    public SisLevel initSisLevel(Long customerId) throws Exception {
        SisLevel level = sisLevelRepository.findByMerchantIdSys(customerId);
        if (Objects.isNull(level)) {
            SisLevel sisLevel = new SisLevel();
            sisLevel.setLevelNo(1);
            sisLevel.setLevelName("一级店铺");
            sisLevel.setMerchantId(customerId);
            sisLevel.setRrmark("");
            sisLevel.setUpShopNum(0);
            sisLevel.setUpTeamShopNum(0);
        }
        return null;
    }

    @Override
    public void givePartnerStock(User user, String orderId,SisConfig sisConfig) throws Exception {
        Long customerId = user.getMerchant().getId();
        MallCptCfg mallCptCfg = mallCptCfgRepository.findOne(customerId);
        if (Objects.isNull(sisConfig)) {
            log.info(user.getMerchant().getId() + "No the inn in inn configuration information for businesses！," +
                    "Can't send shares!");
            return;
        }
        if (Objects.isNull(mallCptCfg)) {
            log.info(user.getMerchant().getId() + "No sent strands of configuration information businesses！," +
                    "Can't send shares!");
            return;
        }
        if (sisConfig.getEnabled() == 0) {
            log.info(user.getMerchant().getId() + "Business is not enabled the inn in inn configuration！," +
                    "Can't send shares!");
            return;
        }
        if (mallCptCfg.getStatus() == 0) {
            log.info(user.getMerchant().getId() + "Business is not enabled to send configuration！," +
                    "Can't send shares!");
            return;
        }

        //自己送股

        //送的股数
        Integer stockNum = sisConfig.getCorpStockSelf();
        //自己的会员ID
        Long memberId = user.getId();
        //贡献的会员ID(默认都是当前会员)
        Long contribMemberId = memberId;

        Long members = mallCptMembersRepository.countByMemberId(memberId);
        if (members > 0) {
            mallCptStockLogService.saveCptStockLogs(customerId, mallCptCfg.getCumulativeAmount(),
                    stockNum, memberId, contribMemberId, orderId);
        }

        //上线送股
        memberId = user.getBelongOne();
        stockNum = sisConfig.getCorpStockBelongOne();
        members = mallCptMembersRepository.countByMemberId(memberId);
        if (memberId > 0 && members > 0) {//有上线，并且上线也是合伙人
            mallCptStockLogService.saveCptStockLogs(customerId, mallCptCfg.getCumulativeAmount(),
                    stockNum, memberId, contribMemberId, orderId);
        }
    }

    @Override
    public SisInviteLog findUserLatestInviteLog(Long acceptId) throws Exception {
        return null;
    }

    @Override
    public User findTotalGenerationTwoByUser(User user) throws Exception {
        //总代2的等级ID
        Long totalTowId=Long.parseLong(
                systemConfigRepository.findOne(
                        SysConfigConstant.Total_Generation_TwoId).getValueForCode());
        Long beloneOneId=user.getBelongOne();
        User totalTowUser=null;
        while (true){
            if(beloneOneId==null||beloneOneId==0){
                break;
            }
            User beloneUser=userRepository.findOne(beloneOneId);
            if(totalTowId.equals((long)beloneUser.getLevelId())){
                totalTowUser=beloneUser;
                break;
            }
            beloneOneId=beloneUser.getBelongOne();
        }
        return totalTowUser;
    }

    @Override
    public Integer getTotalGeneraltionType(Long sisLevelId) throws Exception {
        if(getTotalGeneraltionId(SysConfigConstant.SpecialShop_Id).equals(sisLevelId)){
            return 1;
        }
        if(getTotalGeneraltionId(SysConfigConstant.FlagshipShop_Id).equals(sisLevelId)){
            return 2;
        }
        return null;
    }

    @Override
    public Long getTotalGeneraltionId(String key) throws Exception {
        SystemConfig systemConfig=systemConfigRepository.findOne(key);
        if(systemConfig!=null){
            Long id=Long.parseLong(systemConfig.getValueForCode());
            return id;
        }
        return 0L;
    }

    @Override
    public IndirectPushFlow saveIndirectPushFlow(User user, Order order) throws Exception {
        Sis sis=sisRepository.findByUser(user);
        User totalTwoUser=findTotalGenerationTwoByUser(user);
        if(totalTwoUser==null){
            return null;
        }
        IndirectPushFlow indirectPushFlow=new IndirectPushFlow();
        indirectPushFlow.setOrder(order);
        indirectPushFlow.setOwner(user);
        indirectPushFlow.setTotalGenerationTwoUser(totalTwoUser);
        indirectPushFlow.setTime(new Date().getTime());
        if(sis.getSisLevel()!=null){
            indirectPushFlow.setUserShopLevelType(getTotalGeneraltionType(sis.getSisLevel().getId()));
        }
        indirectPushFlowRepository.save(indirectPushFlow);

        return indirectPushFlow;
    }

    @Override
    public Integer getTotalUserType(Long userId) throws Exception{
        if(getTotalGeneraltionId(SysConfigConstant.Total_Generation_OneId).equals(userId)){
            return 1;
        }
        if(getTotalGeneraltionId(SysConfigConstant.Total_Generation_TwoId).equals(userId)){
            return 2;
        }
        return 0;
    }
}
