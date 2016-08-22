package com.huotu.sis.service.impl;

import com.huotu.common.base.CookieHelper;
import com.huotu.common.base.RSAHelper;
import com.huotu.huobanplus.common.entity.MallCptCfg;
import com.huotu.huobanplus.common.entity.OrderItems;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.repository.MallCptCfgRepository;
import com.huotu.huobanplus.common.repository.MallCptMembersRepository;
import com.huotu.huobanplus.common.repository.UserRepository;
import com.huotu.huobanplus.smartui.entity.TemplatePage;
import com.huotu.huobanplus.smartui.entity.support.Scope;
import com.huotu.huobanplus.smartui.repository.TemplatePageRepository;
import com.huotu.sis.entity.*;
import com.huotu.sis.entity.support.*;
import com.huotu.sis.exception.SisException;
import com.huotu.sis.model.sisweb.SisRebateModel;
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
import java.util.*;

/**
 * Created by lgh on 2015/12/30.
 */
@Service
public class UserServiceImpl implements UserService {

    private static Log log = LogFactory.getLog(UserServiceImpl.class);
    private String userKey = "userId";
    private String loginApp = "loginApp";
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
    private SisConfigRepository sisConfigRepository;

    @Autowired
    private MallAdvanceLogsService mallAdvanceLogsService;

    @Autowired
    private SisService sisService;

    @Autowired
    private MallCptMembersRepository mallCptMembersRepository;
    @Autowired
    private MallCptCfgRepository mallCptCfgRepository;
    @Autowired
    private MallCptStockLogService mallCptStockLogService;

    @Autowired
    private SisOpenAwardLogService sisOpenAwardLogService;

    @Autowired
    private SisInviteRepository sisInviteRepository;

    @Autowired
    private SisOrderItemsRepository sisOrderItemsRepository;

    @Autowired
    private SisOpenAwardAssignRepository sisOpenAwardAssignRepository;

    @Autowired
    private SisLevelService sisLevelService;

    @Override
    public Long getUserId(HttpServletRequest request) {
        if (env.acceptsProfiles("develop")) {
//            userRepository.findAll();
            return 97278L;//146 4471商户 王明
//            return 96116L;
        } else {
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
    public void setLoginType(HttpServletRequest request, HttpServletResponse response, String loginType) throws Exception {
        String encrypt = RSAHelper.encrypt(loginType, publicKey);
        CookieHelper.set(response, loginApp, encrypt, request.getServerName(), 60 * 60 * 24 * 365);

    }

    @Override
    public String getLoginType(HttpServletRequest request) throws Exception {
//        if (env.acceptsProfiles("develop")){
//            return "1";
//        }
        String encrypt = CookieHelper.get(request, loginApp);
        if (encrypt == null) {
            return "0";
        }
        try {
            String type = RSAHelper.decrypt(encrypt, privateKey);
            if ("1".equals(type)) {
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
    public void newOpen(User user, String orderId, SisConfig sisConfig) throws Exception {
        Sis sis = sisRepository.findByUser(user);
        List<TemplatePage> templatePage = templatePageRepository.findByScopeAndEnabled(Scope.sis, true);
        SisInviteLog sisInviteLog = sisInviteRepository.findFirstByAcceptIdOrderByAcceptTimeDesc(user.getId());
        //开店用户等级设置
        SisLevel sisLevel = sisLevelRepository.findByMerchantIdSys(user.getMerchant().getId());

        if(sisLevel==null){
            sisLevel=sisLevelRepository.findFirstByMerchantIdOrderByLevelNoAsc(user.getMerchant().getId());
        }
        if(sisLevel==null){
            throw new SisException("customerId:"+user.getMerchant().getId()+"have no sisLevel");
        }


        //多个开店商品，有等级
        if (sisConfig.getOpenMode() == 1) {//todo 开店商品模式修改
            OrderItems orderItems = sisOrderItemsRepository.getOrderItemsByOrderId(orderId).get(0);
            OpenGoodsIdLevelIds openGoodsIdLevelIds = sisConfig.getOpenGoodsIdlist();
            //根据订单号找到该用户购买的开店等级
            for (OpenGoodsIdLevelId ol : openGoodsIdLevelIds.values()) {
                if (orderItems.getGoodsId() != null) {
                    if (Long.valueOf(orderItems.getGoodsId()).equals(ol.getGoodsid())) {
                        sisLevel = sisLevelRepository.findOne(ol.getLevelid());
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
            if (sisInviteLog != null) {
                if (!StringUtils.isEmpty(sisInviteLog.getRealName())) {
                    sis.setTitle(sisInviteLog.getRealName() + "的小店");
                }
                sis.setRealName(sisInviteLog.getRealName());
                sis.setMobile(sisInviteLog.getMobile());
            }
        }
        sisRepository.save(sis);
        log.debug(user.getId()+"openShopOver");
    }

    @Override
    public void countOpenShopAward(User user, String orderId, String unionOrderId, SisConfig sisConfig) throws Exception {
        if (!Objects.isNull(sisConfig)) {
            if (sisConfig.getEnabled() == 0 || sisConfig.getOpenMode() == 0) {
                log.info(user.getMerchant().getId() + "Businessman is not enabled the inn in inn " +
                        "configuration or configured with free open a shop, not rebate");
                //店中店配置需要开启，并且是收费的,才有返利
                return;
            }

            //个性化返利模式
            if (sisConfig.getOpenAwardMode() == 1) {
                User belongOne = userRepository.findOne(user.getBelongOne());
                //没有上级，无法返利
                if (belongOne == null) {
                    log.info("user:" + user.getId() + "have no belongOneId");
                    return;
                }
                Sis belongOneSis = sisRepository.findByUser(belongOne);
                if (belongOneSis == null) {
                    log.info("user" + user.getId() + "belongOne have no sisShop");
                    return;
                }
                //获取自己店铺
                Sis ownSis = sisRepository.findByUser(user);
                if (ownSis == null) {
                    log.info("user" + user.getId() + "won have no sisShop");
                    return;
                }
                SisOpenAwardAssign sisOpenAwardAssign = sisOpenAwardAssignRepository.
                        findByLevel_IdAndGuideLevel_Id(
                                belongOneSis.getSisLevel().getId(),
                                ownSis.getSisLevel().getId());
                //上线等级和自己等级无法匹配到返利余额
                if (sisOpenAwardAssign == null) {
                    log.info("user" + user.getId() + "On-line level and their own level can't match to the rebate balance");
                    return;
                }
                //插入一条开店流水
                rebateOpenShop(belongOne, user, sisOpenAwardAssign.getAdvanceVal(), orderId, unionOrderId, 1);
                String memo = "1级会员(" + user.getWxNickName() + ")贡献了开店奖";
                //插入一条开店返利日志
                sisOpenAwardLogService.saveSisOpenAwardLog(belongOne.getMerchant().getId()
                        , belongOne, user, sisOpenAwardAssign.getAdvanceVal(), 1, orderId);
            }


            //默认八层返利模式
            if (sisConfig.getOpenAwardMode() == null || sisConfig.getOpenAwardMode() == 0) {
                OpenSisAwards openSisAwards = sisConfig.getOpenSisAwards();
                if (!Objects.isNull(openSisAwards)) {
                    //获取开店奖设置的层级
                    Integer tiers = openSisAwards.size();
                    if (tiers > 0) {
                        User rebateUser = user;//默认从自己开始返利

                        double rebateMonery = 0;
                        //遍历每一个层级的开店返利信息
                        for (int i = 0; i < tiers; i++) {
                            if(rebateUser==null){
                                break;
                            }
                            Sis sis = sisRepository.findByUser(rebateUser);
                            if (Objects.isNull(sis)) {
                                //用户未开启店中店，无法返利
                                log.info("user=" + user.getId() + "Not to open the inn in inn to rebate！");
                                //获取他的上级用户
                                rebateUser = userRepository.findOne(rebateUser.getBelongOne());
                                continue;
                            }
                            if (!sis.isStatus()) {
                                //用户店中店被关闭,无法返利
                                log.info("user=" + user.getId() + "Users to the inn in inn is closed to rebate！");
                                //获取他的上级用户
                                rebateUser = userRepository.findOne(rebateUser.getBelongOne());
                                continue;
                            }
                            //获取返利信息
                            OpenSisAward openSisAward = openSisAwards.get((long) i);
                            //是否是个性化开店返利
                            if (openSisAward.getUnified() == -1.0) {//是个性化
                                //获取用户的店铺等级ID
                                long levelId = sisService.getSisLevelId(rebateUser);
                                for (LevelIdAndVal lv : openSisAward.getCustom()) {
                                    if (lv.getLvid() == levelId) {//如果店铺的ID等于某个个性化返利的ID
                                        //开店奖返利
                                        rebateMonery = lv.getVal();
                                        break;
                                    }
                                }
                            } else if (openSisAward.getUnified() >= 0) {//不是个性化
                                rebateMonery = openSisAward.getUnified();
                            }
                            //---------记录----------
                            //给某个用户开店奖返利,包括插入流水
                            rebateOpenShop(rebateUser, user, rebateMonery, orderId, unionOrderId, i);
                            log.debug(user.getId() + "");
                            //插入一条开店返利日志
                            String memo = i + "级会员(" + user.getWxNickName() + ")贡献了开店奖";
                            sisOpenAwardLogService.saveSisOpenAwardLog(user.getMerchant().getId(), rebateUser,
                                    user, rebateMonery, i, orderId);

                            //---------记录END----------

                            //为下个层级返利做准备
                            if (rebateUser.getBelongOne() <= 0) {
                                //没有上级
                                break;
                            }
                            //获取他的上级用户
                            rebateUser = userRepository.findOne(rebateUser.getBelongOne());
                        }
                    }
                } else {
                    log.info("merchant=" + user.getMerchant().getId() + "Merchants set up shop level configuration error, " +
                            "cannot rebate!");
                }
            }
        } else {
            log.info("merchant=" + user.getMerchant().getId() + "No the inn in inn configuration information for businesses" +
                    "cannot rebate!");
        }

    }

    @Override
    public void newCountOpenShopAward(User user, String orderId, String unionOrderId, SisConfig sisConfig) throws Exception {
        if (sisConfig.getEnabled() == 0 || sisConfig.getOpenMode() == 0) {
            log.info(user.getMerchant().getId() + "Businessman is not enabled the inn in inn " +
                    "configuration or configured with free open a shop, not rebate");
            //店中店配置需要开启，并且是收费的,才有返利
            return;
        }
        Sis sis=sisRepository.findByUser(user);
        if(sis==null||sis.getSisLevel()==null){
            return;
        }

        SisLevelAwards sisLevelOpenAwards=sisConfig.getSisLevelOpenAwards();
        //兼容旧数据
        if(sisLevelOpenAwards==null){
            sisLevelOpenAwards=oldOpenCompatibility(user,sis,sisConfig);
        }

        List<SisRebateModel> sisRebateModels= getSisRebateModelList(user,sisLevelOpenAwards.get(sis.getSisLevel().getId()));

        //逐个返利
        for(int i=0,size=sisRebateModels.size();i<size;i++){
            SisRebateModel sisRebateModel=sisRebateModels.get(i);
            //增加返利和流水日志
            rebateOpenShop(sisRebateModel.getUser(),user,sisRebateModel.getRebate(),orderId,unionOrderId,i);
            //插入一条开店返利日志
            sisOpenAwardLogService.saveSisOpenAwardLog(user.getMerchant().getId(),sisRebateModel.getUser(),
                    user, sisRebateModel.getRebate(), i, orderId);
        }
        log.debug(user.getId() + "openCountOver");
    }

    @Override
    public void rebateOpenShop(User earningsUser, User contributeUser, Double money, String orderId, String unionOrderId, Integer srcType) throws Exception {
        //增加用户余额
        saveUserBalance(earningsUser,money);
        //插入一条开店奖流水
        mallAdvanceLogsService.saveMallAdvanceLogs(earningsUser, contributeUser, money, orderId, unionOrderId, srcType);
    }


    @Override
    public SisLevel initSisLevel(Long customerId) throws Exception {
        SisLevel level = sisLevelRepository.findByMerchantIdSys(customerId);
        if (Objects.isNull(level)) {
            SisLevel sisLevel = new SisLevel();
            sisLevel.setLevelNo(0);
            sisLevel.setIsSystem(1);
            sisLevel.setLevelName("一级店铺");
            sisLevel.setMerchantId(customerId);
            sisLevel.setRrmark("");
            sisLevel.setUpShopNum(0);
            sisLevel.setUpTeamShopNum(0);
        }
        return null;
    }

    @Override
    public void givePartnerStock(User user, String orderId, SisConfig sisConfig) throws Exception {
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

        if(sisConfig.getOpenMode()==0){
            log.info(user.getMerchant().getId()+"configured with free open a shop, not rebate");
            return;
        }

        if (mallCptCfg.getStatus() == 0) {
            log.info(user.getMerchant().getId() + "Business is not enabled to send configuration！," +
                    "Can't send shares!");
            return;
        }
        Sis sis=sisRepository.findByUser(user);
        if(sis==null||sis.getSisLevel()==null){
            return;
        }
        SisLevelAwards sisLevelStockAwards=sisConfig.getSisLevelStockAwards();

        if(sisLevelStockAwards==null){
            //兼容旧送股数据
            sisLevelStockAwards=oldStockAwardCompatibility(sis.getSisLevel().getId(),sisConfig);
        }

        List<SisRebateModel> sisRebateModels= getSisRebateModelList(user,sisLevelStockAwards.get(sis.getSisLevel().getId()));

        //逐个送股
        for(int i=0,size=sisRebateModels.size();i<size;i++){
            SisRebateModel sisRebateModel=sisRebateModels.get(i);
            Long members = mallCptMembersRepository.countByMemberId(sisRebateModel.getUser().getId());
            int stockNum=(int)sisRebateModel.getRebate();
            //只有是合伙人才能送股
            if (members > 0 && stockNum>0) {
                mallCptStockLogService.saveCptStockLogs(customerId, mallCptCfg.getCumulativeAmount(),
                        stockNum, sisRebateModel.getUser(), user, orderId);
            }
        }
        log.debug(user.getId() + "songguOver");
    }

    @Override
    public SisInviteLog findUserLatestInviteLog(Long acceptId) throws Exception {
        return null;
    }

    @Override
    public List<User> getAllRelationByUserId(Long userId) {
        User user = userRepository.findOne(userId);
        List<User> list = new ArrayList<>();
        if (null != user) {
            list.add(user);
            getParentByUser(user, list);
        }
        return list;
    }

    @Override
    public List<SisRebateModel> getSisRebateModelList(User user, SisLevelAward sisLevelAward) throws Exception {
        List<SisRebateModel> sisRebateModels=new ArrayList<>();

        if(sisLevelAward==null||sisLevelAward.getCfg()==null|| sisLevelAward.getCfg().size()==0){
            return sisRebateModels;
        }
        //返利配置列表
        List<OpenSisAward> openSisAwards=sisLevelAward.getCfg();
        //获取开店奖设置的层级
        Integer tiers = openSisAwards.size();
        //获取指定层级的人物关系列表
        List<User> users=getParentByUser(user,tiers-1);

        for(int i=0,size=users.size();i<size;i++){
            User rebateUser=users.get(i);
            //返利的前提是要先开店
            Sis sis = sisRepository.findByUser(rebateUser);

            OpenSisAward openSisAward=openSisAwards.get(i);

            SisRebateModel sisRebateModel=new SisRebateModel();
            sisRebateModel.setUser(rebateUser);
            sisRebateModel.setRebate(i);
            sisRebateModel.setRebate(getSisRebateModel(openSisAward,sis));
            sisRebateModels.add(sisRebateModel);


        }


        return sisRebateModels;

    }

    @Override
    public List<User> getParentByUser(User user, Integer layer) throws Exception {
        if(layer<0){
            layer=0;
        }
        List<User> users=new ArrayList<>();
        User rebate=user;
        for(int i=0;i<=layer;i++){
            if(rebate==null){
                return users;
            }
            users.add(rebate);
            if(rebate.getBelongOne()==null||rebate.getBelongOne()<=0){
                return users;
            }
            rebate=userRepository.findOne(rebate.getBelongOne());
        }
        return users;
    }

    @Override
    public User getUserById(Long userId) throws Exception {
        if(userId==null||userId<=0){
            return null;
        }
        return userRepository.findOne(userId);
    }

    @Override
    public double getSisRebateModel(OpenSisAward openSisAward,Sis sis) {
        //是否个性化
        double rebate=0;

        if(openSisAward==null||sis==null){
            return rebate;
        }

        if(openSisAward.getUnified()!=-1){
            rebate=openSisAward.getUnified();
        }else {
            List<LevelIdAndVal> custom=openSisAward.getCustom();
            SisLevel sisLevel=sis.getSisLevel();
            if(custom==null||sisLevel==null){
                log.info("user:"+sis.getUser().getId()+"hava no sislevel or no config!");
                return rebate;
            }

            for(int i=0;i<custom.size();i++){
                LevelIdAndVal levelIdAndVal=custom.get(i);
                if(levelIdAndVal!=null&&sisLevel.getId()==levelIdAndVal.getLvid()){
                    rebate=levelIdAndVal.getVal();
                    break;
                }
            }
        }
        return rebate;
    }

    @Override
    public SisLevelAwards oldOpenCompatibility(User user, Sis sis, SisConfig sisConfig) {
        SisLevelAwards sisLevelOpenAwards=new SisLevelAwards();
        //默认八级返利兼容
        if (sisConfig.getOpenAwardMode() == null || sisConfig.getOpenAwardMode() == 0) {

            sisLevelOpenAwards=oldOpenAwardCompatibility(sisConfig);
        }
        //letsgo模式兼容
        if(sisConfig.getOpenAwardMode() == 1){
            sisLevelOpenAwards=oldLetsGoModeOpenAwardCompatibility(user);
        }
        return sisLevelOpenAwards;
    }

    @Override
    public SisLevelAwards oldOpenAwardCompatibility(SisConfig sisConfig) {
        SisLevelAwards sisLevelAwards=new SisLevelAwards();
        OpenSisAwards openSisAwards=sisConfig.getOpenSisAwards();
        if(openSisAwards==null){
            return sisLevelAwards;
        }
        List<OpenSisAward> openSisAwardList=new ArrayList<>();
        for(OpenSisAward o:openSisAwards.values()){
            openSisAwardList.add(o);
        }
        List<SisLevel> sisLevels=sisLevelRepository.findByMerchantIdOrderByLevelNoAsc(sisConfig.getMerchantId());
        sisLevelAwards=setAllSisLevelAwards(sisLevels,openSisAwardList);
        sisConfig.setSisLevelOpenAwards(sisLevelAwards);
        sisConfigRepository.save(sisConfig);
        return sisLevelAwards;
    }

    @Override
    public SisLevelAwards oldLetsGoModeOpenAwardCompatibility(User user) {
        SisLevelAwards sisLevelAwards=new SisLevelAwards();
        User belongOne = userRepository.findOne(user.getBelongOne());
        //没有上级，无法返利
        if (belongOne == null) {
            log.info("user:" + user.getId() + "have no belongOneId");
            return sisLevelAwards;
        }
        Sis belongOneSis = sisRepository.findByUser(belongOne);
        if (belongOneSis == null) {
            log.info("user" + user.getId() + "belongOne have no sisShop");
            return sisLevelAwards;
        }
        //获取自己店铺
        Sis ownSis = sisRepository.findByUser(user);
        if (ownSis == null) {
            log.info("user" + user.getId() + "won have no sisShop");
            return sisLevelAwards;
        }
        SisOpenAwardAssign sisOpenAwardAssign = sisOpenAwardAssignRepository.
                findByLevel_IdAndGuideLevel_Id(
                        belongOneSis.getSisLevel().getId(),
                        ownSis.getSisLevel().getId());
        //上线等级和自己等级无法匹配到返利余额
        if (sisOpenAwardAssign == null) {
            log.info("user" + user.getId() + "On-line level and their own level can't match to the rebate balance");
            return sisLevelAwards;
        }

        SisLevelAward sisLevelAward=new SisLevelAward();
        Long userSisLevelId=sisOpenAwardAssign.getGuideLevel().getId();
        sisLevelAward.setBuySisLvId(userSisLevelId);

        List<OpenSisAward> cfg=new ArrayList<>();
        //自己不返利
        OpenSisAward openSisAward=new OpenSisAward();
        openSisAward.setIdx(0);
        openSisAward.setUnified(0);
        cfg.add(openSisAward);

        //上线返利
        openSisAward=new OpenSisAward();
        openSisAward.setIdx(1);
        openSisAward.setUnified(sisOpenAwardAssign.getAdvanceVal());
        cfg.add(openSisAward);

        sisLevelAward.setCfg(cfg);
        sisLevelAwards.put(userSisLevelId,sisLevelAward);

        return sisLevelAwards;
    }

    @Override
    public SisLevelAwards oldStockAwardCompatibility(Long sisLevelId,SisConfig sisConfig) {
        SisLevelAwards sisLevelStockAwards=new SisLevelAwards();
        List<OpenSisAward> openSisAwards=new ArrayList<>();

        double corpStockSelf=sisConfig.getCorpStockSelf()==null?0:sisConfig.getCorpStockSelf();
        OpenSisAward openSisAward=new OpenSisAward();
        openSisAward.setIdx(0);
        openSisAward.setUnified(corpStockSelf);
        openSisAwards.add(openSisAward);

        double corpStockBelongOne=sisConfig.getCorpStockBelongOne()==null?0:sisConfig.getCorpStockBelongOne();

        openSisAward=new OpenSisAward();
        openSisAward.setIdx(1);
        openSisAward.setUnified(corpStockBelongOne);
        openSisAwards.add(openSisAward);


        List<SisLevel> sisLevels=sisLevelRepository.findByMerchantIdOrderByLevelNoAsc(sisConfig.getMerchantId());

        sisLevelStockAwards=setAllSisLevelAwards(sisLevels,openSisAwards);
        sisConfig.setSisLevelStockAwards(sisLevelStockAwards);
        sisConfigRepository.save(sisConfig);
        return sisLevelStockAwards;
    }

    @Override
    public SisLevelAwards oldPushAwardCompatibility(Long customerId) {
        SisLevelAwards sisLevelAwards=new SisLevelAwards();

        List<SisLevel> sisLevels=sisLevelRepository.findByMerchantIdOrderByLevelNoAsc(customerId);

        if(sisLevels==null){
            return sisLevelAwards;
        }

        for(int i=0,size=sisLevels.size();i<size;i++){
            SisLevel sisLevel= sisLevels.get(i);


            double rebateRate=sisLevel.getRebateRate();
            SisLevelAward sisLevelAward=new SisLevelAward();
            sisLevelAward.setBuySisLvId(sisLevel.getId());

            List<OpenSisAward> openSisAwardList=new ArrayList<>();
            OpenSisAward openSisAward=new OpenSisAward();
            openSisAward.setIdx(0);
            openSisAward.setUnified(rebateRate);
            openSisAwardList.add(openSisAward);

            sisLevelAward.setCfg(openSisAwardList);
            sisLevelAwards.put(sisLevel.getId(),sisLevelAward);

        }

        SisConfig sisConfig=sisConfigRepository.findByMerchantId(customerId);
        sisConfig.setSisLevelPushAwards(sisLevelAwards);
        sisConfigRepository.save(sisConfig);
        return sisLevelAwards;
    }

    @Override
    public SisLevelAwards setAllSisLevelAwards(List<SisLevel> sisLevels, List<OpenSisAward> openSisAwardList) {

        SisLevelAwards sisLevelAwards=new SisLevelAwards();
        if(sisLevels==null){
            return sisLevelAwards;
        }
        sisLevels.forEach(sisLevel -> {
            SisLevelAward sisLevelAward=new SisLevelAward();
            sisLevelAward.setCfg(openSisAwardList);
            sisLevelAward.setBuySisLvId(sisLevel.getId());
            sisLevelAwards.put(sisLevel.getId(),sisLevelAward);
        });
        return sisLevelAwards;
    }

    @Override
    public void saveUserBalance(User user, double money) throws Exception {
        log.debug("user " + user.getId()+" get "+money);
        if (money <= 0) {
            log.info("user" + user.getId() + "rebate is 0");
            return;
        }
        BigDecimal balance = new BigDecimal(user.getUserBalance());
        balance = balance.add(new BigDecimal(money));
        user.setUserBalance(balance.doubleValue());
        userRepository.save(user);

    }

    /**
     * 递归查找上级用户
     *
     * @param user
     * @param list
     * @return
     */
    private List<User> getParentByUser(User user, List<User> list) {
        List<Long> idList = new ArrayList<>();
        if (null != user.getBelongOne() && user.getBelongOne().intValue() > 0 && null != user.getBelongTwo()
                && user.getBelongTwo().intValue() > 0 && null != user.getBelongThree() && user.getBelongThree().intValue() > 0) {

            idList.add(user.getBelongOne());
            idList.add(user.getBelongTwo());
            idList.add(user.getBelongThree());
            List<User> users = userRepository.findByUserIds(idList);
//            上级
            Optional<User> oneBelongUser = users.stream().filter(oneUser -> oneUser.getId().equals(user.getBelongOne())).findAny();
            addUserList(oneBelongUser, list);
//            上上级
            Optional<User> twoBelongUser = users.stream().filter(oneUser -> oneUser.getId().equals(user.getBelongTwo())).findAny();
            addUserList(twoBelongUser, list);
//            上上上级
            User belongThree = null;
            Optional<User> threeBelongUser = users.stream().filter(oneUser -> oneUser.getId().equals(user.getBelongThree())).findAny();
            addUserList(threeBelongUser, list);
            if (threeBelongUser.isPresent())
                belongThree = threeBelongUser.get();
            if (null != belongThree)
                getParentByUser(belongThree, list);

        } else if (null != user.getBelongOne() && user.getBelongOne().intValue() > 0 && null != user.getBelongTwo()
                && user.getBelongTwo().intValue() > 0) {
            idList.add(user.getBelongOne());
            idList.add(user.getBelongTwo());
            List<User> users = userRepository.findByUserIds(idList);
//            上级
            Optional<User> oneBelongUser = users.stream().filter(oneUser -> oneUser.getId().equals(user.getBelongOne())).findAny();
            addUserList(oneBelongUser, list);
//            上上级
            Optional<User> twoBelongUser = users.stream().filter(oneUser -> oneUser.getId().equals(user.getBelongTwo())).findAny();
            addUserList(twoBelongUser, list);
        } else if (null != user.getBelongOne() && user.getBelongOne().intValue() > 0) {
            User belongOne = userRepository.findOne(user.getBelongOne());
            Optional<User> optional = list.stream().filter(baseUser -> baseUser.getId().equals(belongOne.getId())).findAny();
            if (!optional.isPresent())
                list.add(belongOne);
        }
        return list;
    }

    private List<User> addUserList(Optional<User> optionalUser, List<User> list) {
        if (optionalUser.isPresent()) {
            Optional<User> optional = list.stream().filter(baseUser -> baseUser.getId().equals(optionalUser.get().getId())).findAny();
            if (!optional.isPresent()) {
                list.add(optionalUser.get());
            }
        }
        return list;
    }
}
