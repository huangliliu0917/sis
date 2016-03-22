package com.huotu.sis.service.impl;

import com.huotu.common.base.CookieHelper;
import com.huotu.common.base.RSAHelper;
import com.huotu.huobanplus.common.entity.MallCptCfg;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.repository.MallCptCfgRepository;
import com.huotu.huobanplus.common.repository.MallCptMembersRepository;
import com.huotu.huobanplus.common.repository.UserRepository;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisInviteLog;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.entity.support.LevelIdAndVal;
import com.huotu.sis.entity.support.OpenSisAward;
import com.huotu.sis.entity.support.OpenSisAwards;
import com.huotu.sis.repository.SisConfigRepository;
import com.huotu.sis.repository.SisInviteRepository;
import com.huotu.sis.repository.SisLevelRepository;
import com.huotu.sis.repository.SisRepository;
import com.huotu.sis.exception.UserSisShopISOPENException;
import com.huotu.sis.service.*;
import com.huotu.huobanplus.smartui.entity.TemplatePage;
import com.huotu.huobanplus.smartui.entity.support.Scope;
import com.huotu.huobanplus.smartui.repository.TemplatePageRepository;
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

    @Override
    public Long getUserId(HttpServletRequest request) {
        if (env.acceptsProfiles("develop"))
            return 97278L;//146 4471商户 王明
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
//        if (env.acceptsProfiles("develop")){
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
    public void newOpen(User user) throws Exception {
        Sis sis = sisRepository.findByUser(user);
        List<TemplatePage> templatePage = templatePageRepository.findByScopeAndEnabled(Scope.sis, true);
        SisInviteLog sisInviteLog=sisInviteRepository.findFirstByAcceptIdOrderByAcceptTimeDesc(user.getId());
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
            SisLevel sisLevel = sisLevelRepository.findByMerchantIdSys(user.getMerchant().getId());
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
            //查找用户最近填写的邀请信息
        } else {
            log.info(user.getId()+"Can't repeat open a shop!");
            throw new UserSisShopISOPENException("店中店已经开启！");
        }
        sisRepository.save(sis);
    }

    @Override
    public void countOpenShopAward(User user, String orderId, String unionOrderId) throws Exception {
        SisConfig sisConfig = sisConfigRepository.findByMerchantId(user.getMerchant().getId());
        if (!Objects.isNull(sisConfig)) {
            if (sisConfig.getEnabled() == 0 || sisConfig.getOpenMode() == 0) {
                log.info(user.getMerchant().getId() + "Businessman is not enabled the inn in inn " +
                        "configuration or configured with free open a shop, not rebate");
                //店中店配置需要开启，并且是收费的,才有返利
                return;
            }
            OpenSisAwards openSisAwards = sisConfig.getOpenSisAwards();
            if (!Objects.isNull(openSisAwards)) {
                //获取开店奖设置的层级
                Integer tiers = openSisAwards.size();
                if (tiers > 0) {
                    User rebateUser = user;//默认从自己开始返利

                    double rebateMonery = 0;
                    //遍历每一个层级的开店返利信息
                    for (int i = 0; i < tiers; i++) {
                        Sis sis = sisRepository.findByUser(rebateUser);
                        if (Objects.isNull(sis)) {
                            //用户未开启店中店，无法返利
                            log.info("merchant=" + user.getMerchant().getId() + "Not to open the inn in inn to rebate！");
                            //获取他的上级用户
                            rebateUser = userRepository.findOne(rebateUser.getBelongOne());
                            continue;
                        }
                        if (!sis.isStatus()) {
                            //用户店中店被关闭,无法返利
                            log.info("merchant=" + user.getMerchant().getId() + "Users to the inn in inn is closed to rebate！");
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
                        rebateOpenShop(rebateUser,user,rebateMonery, orderId, unionOrderId,i);
                        log.info(user.getId()+"");
                        //插入一条开店返利日志
                        String memo = i + "级会员(" + user.getWxNickName() + ")贡献了开店奖";
                        sisOpenAwardLogService.saveSisOpenAwardLog(user.getMerchant().getId(), rebateUser.getId(),
                                user.getId(), rebateMonery, memo, i, orderId);

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
        } else {
            log.info("merchant=" + user.getMerchant().getId() + "No the inn in inn configuration information for businesses" +
                    "cannot rebate!");
        }

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
    public void givePartnerStock(User user, String orderId) throws Exception {
        Long customerId = user.getMerchant().getId();
        SisConfig sisConfig = sisConfigRepository.findByMerchantId(customerId);
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
}
