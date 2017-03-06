package com.huotu.sis.interceptor;

import com.huotu.common.base.HttpHelper;
import com.huotu.huobanplus.common.UserType;
import com.huotu.huobanplus.common.entity.Merchant;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.common.PublicParameterHolder;
import com.huotu.sis.common.StringHelper;
import com.huotu.sis.exception.SisException;
import com.huotu.sis.model.sisweb.PublicParameterModel;
import com.huotu.sis.repository.mall.MerchantRepository;
import com.huotu.sis.repository.mall.UserRepository;
import com.huotu.sis.service.CommonConfigsService;
import com.huotu.sis.service.SecurityService;
import com.huotu.sis.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用用户拦截
 * 没有登录的情况下进行调整
 * Created by lgh on 2015/12/30.
 */
public class CommonUserInterceptor implements HandlerInterceptor {

    private static Log log = LogFactory.getLog(CommonUserInterceptor.class);
    @Autowired
    protected SecurityService securityService;
    @Autowired
    private CommonConfigsService commonConfigService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private Environment env;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long userId=0L;
        String userAgent=request.getHeader("User-Agent");
        log.debug("useragent"+userAgent);
        log.debug("url:"+request.getRequestURL()+" canshu:"+request.getQueryString());
        String[] data= StringHelper.getRequestAppInfo(userAgent);
        if(data!=null&&StringHelper.isTrueSign(data)){
                userId=Long.parseLong(data[1]);
        }else {
            Long customerId=getCurrentCustomerId(request);
            Merchant merchant=merchantRepository.getOne(customerId);
            if(merchant.getAccountModle()==3){
                userId=userService.currentUserId(request,customerId);
                if(!request.getRequestURI().contains("showOpenShop")){
                    if(userId==null){
                        String domain=userService.getMerchantSubDomain(customerId);
                        String gduid= request.getParameter("gduid");
                        String backUrl=request.getRequestURL()+"?"+request.getQueryString();
                        String encodeUrl=URLEncoder.encode(backUrl,"utf-8");
                        mallRegister(response,domain,customerId,encodeUrl,gduid);
                        return false;
                    }
                }

            }else {
                userId = userService.getUserId(request);
                String paramUserId = request.getParameter("mainUserId");
                Boolean toSSO =isSSO(userId,paramUserId,customerId);
                //进行单点登录
                if (toSSO) {
                    String forceRefresh=forceRefresh(userId,paramUserId,customerId);
                    sso(request,response,customerId,forceRefresh);
                    return false;
                }

                if (!StringUtils.isEmpty(request.getParameter("gduid")) && Integer.parseInt(request.getParameter("gduid")) > 0) {
                    User user = userRepository.findOne(userId);
                    if (user.getUserType().equals(UserType.normal) && user.getBelongOne() == 0) {
                        //todo 调用王明的找回逻辑服务 test
                        Map<String, String> map = new HashMap<>();
                        map.put("memberid", userId.toString());
                        map.put("gduid", request.getParameter("gduid"));
                        map.put("customerid", user.getMerchant().getId().toString());
                        map.put("appid", commonConfigService.getAppId());
                        map.put("timestamp", String.valueOf((new Date()).getTime()));
                        map.put("sign", securityService.getMapSignByAppSecret(map));
                        //生成toUrl
                        String toUrl = "";
                        for (String key : map.keySet()) {
                            toUrl += "&" + key + "=" + URLEncoder.encode(map.get(key), "utf-8");
                        }
                        String url = commonConfigService.getMallApiWebUrl() + "/account/adoptmember?" + toUrl.substring(1);
                        log.debug(url);
                        HttpHelper.getRequest(url);
                    }

                }

            }
        }

//        log.info("userId is" + userId.toString());

        PublicParameterModel publicParameterModel = new PublicParameterModel();
        publicParameterModel.setUserId(userId);
        PublicParameterHolder.set(publicParameterModel);

//        //没有店进行开店
//        userService.open(userId);



        return true;
    }

    /**
     * 判断是否需要单点登录
     * @return
     */
    private boolean isSSO(Long userId,String paramUserId,Long customerId){
        boolean toSSO = false;
        //强制授权
        if (userId == null || userId == 0) {
            toSSO = true;
        } else if (!StringUtils.isEmpty(paramUserId) && !userId.toString().equals(paramUserId)) {
            //用户切换 强制刷新
            toSSO = true;
        } else {
            //商家切换 强制刷新
            User user = userRepository.findOne(userId);
            if (!user.getMerchant().getId().equals(customerId)) {
                toSSO = true;
            }
        }
        return toSSO;
    }

    /**
     * 没有cookie,去商城登录
     */
    private void mallRegister(HttpServletResponse response,String domain,Long customerId,String backSkipUrl
            ,String guideUserId) throws Exception{
        String url=userService.getMallAccreditUrl(backSkipUrl,domain,customerId.toString(),guideUserId);
        response.sendRedirect(url);

    }

    /**
     * 获取当前商户ID
     * @param request
     * @return
     * @throws SisException
     */
    private Long getCurrentCustomerId(HttpServletRequest request) throws SisException{
        String customerIdStr=request.getParameter("customerId");
        if(customerIdStr==null){
            customerIdStr=request.getParameter("customerid");
        }
        if(customerIdStr==null){
            throw new SisException("未获取到商户ID");
        }
        Long customerId = Long.parseLong(customerIdStr);
        return customerId;
    }


    /**
     * 强制刷新：0：否，1：是
     * @param userId
     * @param paramUserId
     * @param customerId
     * @return
     */
    private String forceRefresh(Long userId,String paramUserId,Long customerId){
        //强制刷新用户
        String forceRefresh = "0";
        if(userId==null||userId==0){
            return forceRefresh;
        }
        if(!StringUtils.isEmpty(paramUserId) && !userId.toString().equals(paramUserId)){
            forceRefresh = "1";
        }else {
            User user = userRepository.findOne(userId);
            if (!user.getMerchant().getId().equals(customerId)) {
                forceRefresh = "1";
            }
        }
        return forceRefresh;

    }

    private void sso(HttpServletRequest request,HttpServletResponse response,Long customerId,String forceRefresh) throws
            Exception{
        //todo customerId为空
        String redirectUrl = commonConfigService.getWebUrl() + "/sisweb/auth?redirectUrl=" + URLEncoder.encode(request.getRequestURL()
                + (StringUtils.isEmpty(request.getQueryString()) ? "" : "?" + request.getQueryString()), "utf-8");

        //生成sign
        Map<String, String> map = new HashMap<>();
        map.put("customerId", customerId.toString());
        map.put("redirectUrl", redirectUrl);
        map.put("forceRefresh", forceRefresh);
        String sign = securityService.getMapSign(map);

        //生成toUrl
        String toUrl = "";
        for (String key : map.keySet()) {
            toUrl += "&" + key + "=" + URLEncoder.encode(map.get(key), "utf-8");
        }
        toUrl = commonConfigService.getAuthWebUrl() + "/api/login?" + (toUrl.length() > 0 ? toUrl.substring(1) : "");

//                log.info("interceptor " + toUrl + " " + sign);
        response.sendRedirect(toUrl + "&sign=" + sign);

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
