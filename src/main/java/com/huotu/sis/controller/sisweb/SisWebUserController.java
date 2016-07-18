package com.huotu.sis.controller.sisweb;

import com.huotu.common.base.HttpHelper;
import com.huotu.huobanplus.base.toolService.ResourceService;
import com.huotu.huobanplus.common.UserType;
import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.MerchantConfig;
import com.huotu.huobanplus.common.entity.Product;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.entity.support.ProductSpecifications;
import com.huotu.huobanplus.common.repository.GoodsRepository;
import com.huotu.huobanplus.common.repository.MerchantConfigRepository;
import com.huotu.huobanplus.common.repository.MerchantRepository;
import com.huotu.huobanplus.common.repository.UserRepository;
import com.huotu.huobanplus.model.type.MallEmbedResource;
import com.huotu.huobanplus.sdk.mall.service.MallInfoService;
import com.huotu.huobanplus.smartui.entity.TemplatePage;
import com.huotu.huobanplus.smartui.entity.support.Scope;
import com.huotu.huobanplus.smartui.repository.TemplatePageRepository;
import com.huotu.huobanplus.smartui.sdk.SmartPageRepository;
import com.huotu.sis.common.*;
import com.huotu.sis.entity.*;
import com.huotu.sis.entity.support.OpenGoodsIdLevelId;
import com.huotu.sis.entity.support.OpenGoodsIdLevelIds;
import com.huotu.sis.exception.*;
import com.huotu.sis.model.sisweb.*;
import com.huotu.sis.repository.*;
import com.huotu.sis.service.*;
import com.huotu.sis.service.impl.NoteSendHandle;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by lgh on 2015/12/29.
 */
@Controller
@RequestMapping("/sisweb")
public class SisWebUserController {

    private static Log log = LogFactory.getLog(SisWebUserController.class);
    @Autowired
    SisInviteRepository sisInviteRepository;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private SisRepository sisRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TemplatePageRepository templatePageRepository;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private SmartPageRepository resourceRepository;
    @Autowired
    private MallInfoService mallInfoService;
    private String defaultHead = "/SisHead/default.png";
    private static final String tempImg = "/images/store-back.jpg";
    @Autowired
    private CommonConfigsService commonConfigService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UserService userService;
    @Autowired
    private Environment environment;

    @Autowired
    private SisConfigRepository sisConfigRepository;

    @Autowired
    private SisQualifiedMemberRepository sisQualifiedMemberRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private MerchantConfigRepository merchantConfigRepository;

    @Autowired
    private NoteService noteService;

    @Autowired
    private SisLevelService sisLevelService;

    @Autowired
    private VerificationServiceSelector verificationServiceSelector;

    @Autowired
    private SisLevelRepository sisLevelRepository;

    @Autowired
    private SisOpenAwardAssignRepository sisOpenAwardAssignRepository;


    VerificationService verificationService;


    @PostConstruct
    public void init() {
        verificationService = verificationServiceSelector.forMerchant(
                Long.parseLong(commonConfigService.getNoteSendId()));
    }


    /**
     * 安全授权返回
     * 授权成功后，进行passport安全校验，成功后设置cookie
     *
     * @param token
     * @param sign
     * @param code
     * @return
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("/auth")
    public String auth(String token, String sign, Integer code, String redirectUrl, HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        log.info("enter auth " + token + " " + sign);
        //进行校验
        if (sign == null || !sign.equals(securityService.getSign(request))) {
            return "redirect:/html/error";
        }

        log.info("auth sign passed");
        log.info(redirectUrl);

        if (code == 1) {
            //进行授权校验
            //生成sign
            Map<String, String> map = new HashMap<>();
            map.put("token", token);
            String toSign = securityService.getMapSign(map);
            //生成toUrl

            String toUrl = "";
            for (String key : map.keySet()) {
                toUrl += "&" + key + "=" + URLEncoder.encode(map.get(key), "utf-8");
            }

            toUrl = commonConfigService.getAuthWebUrl() + "/api/check?" + (toUrl.length() > 0 ? toUrl.substring(1) : "");
            String responseText = HttpHelper.getRequest(toUrl + "&sign=" + toSign);

            log.info(responseText);

            if (JsonPath.read(responseText, "$.resultCode").equals(1)) {
                Long userId = Long.parseLong(JsonPath.read(responseText, "$.resultData.data").toString());
                userService.setUserId(userId, request, response);
                log.info("get userId and save in cookie");
                return "redirect:" + redirectUrl;
            }
        }

        log.info("auth error " + code);
        return "redirect:/html/error";
    }


    /**
     * 保存修改店铺信息
     * <b>负责人：徐和康</b>
     *
     * @param sisModel 前台传过来的参数
     * @param request  前台请求
     * @return 店中店数据
     */
    @RequestMapping(value = "/updateSisProfile", method = RequestMethod.POST)
    public String updateSisProfile(SisModel sisModel, Long customerId, HttpServletRequest request) throws IOException {

        if (sisModel.getSisName().length() < 20) {
            PublicParameterModel ppm = PublicParameterHolder.get();
            Long userId = ppm.getUserId();

            User user = userRepository.findOne(userId);
            Sis sis = sisRepository.findByUser(user);

            //todo 上传头像
//            String contextPath = request.getContextPath();
//            Uri uri = new Uri(sisModel.getHead());

            //String imgPath = sisModel.getHead();//uri.getPath().substring(uri.getPath().indexOf("/_resources") + 11);//contextPath.length());
            //sisModel.setHead(resourceService.getResource(imgPath).getURL().toString());

            //删除以前的资源
//            if (sisModel.getHead() != null) {
//                resourceService.deleteResource(sis.getImgPath());
//            }

            //sis.setImgPath(imgPath);
            sis.setShareDesc(sisModel.getShareDetail());

            sis.setShareTitle(sisModel.getShareTitle());
            sis.setTitle(sisModel.getSisName());
            sis.setDescription(sisModel.getSisDetail());
            sisRepository.saveAndFlush(sis);
            request.setAttribute("customerId", customerId);
            return "sisweb/sisHome";
        }
        //sis初始化

        return "redirect:getSisInfo?customerId=" + customerId;

    }

//    /**
//     * 图片上传
//     *
//     * @param sisImage 图片文件
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping(value = "/saveImage")
//    @ResponseBody
//    public Msg saveImage(@RequestParam(value = "sisImage") MultipartFile sisImage) throws Exception {
//        Msg resultModel = new Msg();
////        MultipartFile file=request.getFile("shareImage");
//        //文件格式判断
//        if (ImageIO.read(sisImage.getInputStream()) == null) {
//            resultModel.setCode(0);
//            resultModel.setMsg("请上传图片文件！");
//            return resultModel;
//        }
//        if (sisImage.getSize() == 0) {
//            resultModel.setCode(0);
//            resultModel.setMsg("请上传图片！");
//            return resultModel;
//        }
//        //保存图片
//        String fileName = "/SisHead/"
//                + UUID.randomUUID().toString() + ".png";
//        resourceService.uploadResource(fileName, sisImage.getInputStream());
//        resultModel.setRelativeUrl(fileName);
//        resultModel.setUrl(resourceService.getResource(fileName).getURL().toString());
//        resultModel.setCode(200);
//        resultModel.setMsg("success");
//        return resultModel;
//
//    }
//    /**
//     * 上传店铺图片
//     * <b>负责人：徐和康</b>
//     *
//     * @param imagedata 前台传过来图片
//     * @return 店中店数据
//     */
//    @RequestMapping("/saveImage")
//    @ResponseBody
//    public Msg saveImage( String imagedata) throws IOException {
//
//        //todo 数据待定
//        Msg msg=new Msg();
//        PublicParameterModel ppm = PublicParameterHolder.get();
//        Long userId = ppm.getUserId();
//        User user = userRepository.findOne(userId);
//        Sis sis = sisRepository.findByUser(user);
//
//        String imageData = imagedata.substring(30);
//        //byte[]headPic=imageData.getBytes();
//        byte[] headPic = Base64.getMimeDecoder().decode(imageData);
//        BufferedImage image = ImageIO.read(new ByteArrayInputStream(headPic));
//        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//        ImageIO.write(image, "png", buffer);
//        String fileName = "/SisHead/"
//                + UUID.randomUUID().toString() + ".png";
//
//        File w2 = new File("D://"+fileName);
//        ImageIO.write(ImageIO.read(new ByteArrayInputStream(headPic)), "png", w2);
//        resourceService.uploadResource(fileName, new ByteArrayInputStream(buffer.toByteArray()));
//        //Uri uri = new Uri(fileName);
//        msg.setUrl(resourceService.getResource(fileName).getURL().toString());
//        msg.setMsg("success");
//        msg.setCode(200);
//        return msg;
//    }

//    /**
//     * 跳转到模板列表
//     * <b>负责人：徐和康</b>
//     * @return
//     */
//    @RequestMapping("/jumpToChooseTemplate")
//    public String jumpToChooseTemplate() throws IOException {
//        return "sisweb/chooseTemplate";
//    }

    /**
     * 进入模板列表页面
     *
     * @param request 前台请求
     *                <b>负责人：徐和康</b>
     * @return
     */
    @RequestMapping("/getTemplateList")
    public String getTemplateList(Long customerId, HttpServletRequest request, Model model) throws IOException {
        //todo 进行当前店铺的模板获取
        PublicParameterModel ppm = PublicParameterHolder.get();
        Long userId = ppm.getUserId();

        User user = userRepository.findOne(userId);
        Sis sis = sisRepository.findByUser(user);
        List<TemplatePage> templatePage = templatePageRepository.findByScopeAndEnabled(Scope.sis, true);
        List<TemplatePageModel> list = new ArrayList<>();
//        List<TemplatePage> templatePage = resourceRepository.getListByScope(1);
//        templatePage.stream().forEach(r->{
//            TemplatePageModel templatePageModel = new TemplatePageModel();
//            templatePageModel.setTemplateId(r.getId());
//            templatePageModel.setTitle(r.getTitle());
//            if(!Objects.isNull(r.getThumbnailResource())){
//                log.debug("smartui old img:"+r.getThumbnailResource().getValue());
//                templatePageModel.setImgPath(r.getThumbnailResource().getValue());
//            }
//        });
//        smartui 图片地址，现在不对
        templatePage.stream().forEach(r -> {
            TemplatePageModel templatePageModel = new TemplatePageModel();
            if (!Objects.isNull(r.getThumbnailResource())) {
//                    log.debug("smartui old img:" + r.getThumbnailResource().getValue());
                String path="";
                try {
                    path = resourceRepository.getPathById(r.getId());
                } catch (Exception e) {
                    log.debug("smartui resource error" + e);
                }
//                    String path = resourceService.getResource(r.getThumbnailResource().getValue()).getURL().toString();
                templatePageModel.setImgPath(path);
                if (StringUtils.isEmpty(path))
                    templatePageModel.setImgPath(tempImg);
                log.debug("smartui img:" + path);
            }
            templatePageModel.setTemplateId(r.getId());
            templatePageModel.setTitle(r.getTitle());
            list.add(templatePageModel);

        });
        model.addAttribute("templatePage", list);
        model.addAttribute("templateId", sis.getTemplateId());
        model.addAttribute("customerId", customerId);
        return "sisweb/chooseTemplate";
    }

    /**
     * 选择模板
     * <b>负责人：徐和康</b>
     *
     * @param templateId 模板id
     * @return
     */
    @RequestMapping("/updateTemplate")
    @ResponseBody
    public Msg updateTemplate(Long templateId, Long customerId) {
        Msg msg = new Msg();
        //todo 修改店铺模
        PublicParameterModel ppm = PublicParameterHolder.get();
        Long userId = ppm.getUserId();

        User user = userRepository.findOne(userId);
        Sis sis = sisRepository.findByUser(user);

        sis.setTemplateId(templateId);
        sisRepository.saveAndFlush(sis);
        msg.setCode(200);
        msg.setMsg("success");
        return msg;
    }


    /**
     * 进入修改店铺信息页面
     *
     * @param request 前台请求
     *                <b>负责人：徐和康</b>
     * @return
     */
    @RequestMapping("/getSisInfo")
    public String getSisInfo(Long customerId, HttpServletRequest request) throws IOException {
        //todo 进行当前店铺的模板获取
        PublicParameterModel ppm = PublicParameterHolder.get();
        Long userId = ppm.getUserId();

        User user = userRepository.findOne(userId);
        Sis sis = sisRepository.findByUser(user);

        SisModel sisModel = new SisModel();
        sisModel.setShareTitle(sis.getShareTitle());
        if (Objects.isNull(user.getWeixinImageUrl())) {
            sisModel.setHead("images/moren.png");
        } else
            sisModel.setHead(user.getWeixinImageUrl());
        sisModel.setShareDetail(sis.getShareDesc());
        sisModel.setSisDetail(sis.getDescription());
        sisModel.setSisName(sis.getTitle());
        request.setAttribute("sisModel", sisModel);
        request.setAttribute("customerId", customerId);
        return "sisweb/updateSis";
    }

    /**
     * 进入店铺设置页面
     *
     * @param request 前台请求
     *                <b>负责人：徐和康</b>
     * @return
     */
    @RequestMapping("/jumpToHome")
    public String jumpToHome(Long customerId, HttpServletRequest request) throws IOException {
        PublicParameterModel ppm = PublicParameterHolder.get();
        Long userId = ppm.getUserId();
        User user = userRepository.findOne(userId);
        Sis sis = sisRepository.findByUser(user);
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(customerId);
        SisModel sisModel = new SisModel();
        sisModel.setSisName(sis.getTitle());
        sisModel.setSisDetail(sis.getDescription());
        sisModel.setShareDetail(sis.getShareDesc());
        sisModel.setShareTitle(sis.getShareTitle());
        if (Objects.isNull(user.getWeixinImageUrl())) {
            sisModel.setHead("images/moren.png");
        } else
            sisModel.setHead(user.getWeixinImageUrl());
        request.setAttribute("sisModel", sisModel);
        request.setAttribute("customerId", customerId);
        if(sisConfig!=null){
            request.setAttribute("enableLevelUpgrade",sisConfig.getEnableLevelUpgrade());
        }
        return "sisweb/sisHome";
    }

    private AppSisBaseInfoModel toSisBaseInfoModel(Sis sis, User user) throws IOException {
        //分享二维码的URL
        String indexUrl = getSisCustomerUrl(user.getMerchant().getId(), user.getId());

        AppSisBaseInfoModel appSisBaseInfoModel = new AppSisBaseInfoModel();
        appSisBaseInfoModel.setUserId(user.getId());
        appSisBaseInfoModel.setEnableSis(sis.isStatus());

        if (StringUtils.isEmpty(sis.getImgPath()))
            appSisBaseInfoModel.setImgUrl(resourceService.getResource(defaultHead).getURL().toString());
        else
            appSisBaseInfoModel.setImgUrl(resourceService.getResource(sis.getImgPath()).getURL().toString());
        appSisBaseInfoModel.setSisId(sis.getId());
        appSisBaseInfoModel.setTitle(sis.getTitle());
        appSisBaseInfoModel.setSisDescription(sis.getDescription());
        appSisBaseInfoModel.setShareTitle(sis.getShareTitle());
        appSisBaseInfoModel.setShareDescription(sis.getShareDesc());
        appSisBaseInfoModel.setIndexUrl(indexUrl);
        return appSisBaseInfoModel;
    }

    /**
     * 获取商家域名
     * 负责人：史利挺
     *
     * @param merchantId 商家ID
     * @param userId     用户ID
     * @return
     * @throws IOException
     */
    private String getSisCustomerUrl(long merchantId, long userId) throws IOException {
        String url = getMerchantSubDomain(merchantId);
        url = url.replace("http://", "http://s" + userId + ".");
        StringBuilder sb = new StringBuilder(url);
        sb.append("/shop.aspx?customerid=" + merchantId + "&gduid=" + userId);
        return sb.toString();
    }


    /**
     * 发送验证码
     * 负责人：史利挺
     *
     * @param phone 手机号
     * @param type  场景
     * @return
     * @throws Exception
     */
    @RequestMapping("/sendSMS")
    @ResponseBody
    public ResultModel sendSMS(String phone, int type, Long customerId) throws Exception {
        ResultModel resultModel = new ResultModel();
        // 发送短信前验证
        if (!SysRegex.IsValidMobileNo(phone)) {
            resultModel.setCode(501);
            resultModel.setMessage("不是手机号");
            return resultModel;
        }
        log.info("phone:" + phone + "type:" + type + "merchantId" + customerId);
        try {
            NoteSendHandle.sendCode(noteService.setNoteModel(phone, type, customerId), verificationService);
        } catch (NoteSendException ex) {
            resultModel.setCode(501);
            resultModel.setMessage(ex.getMessage());
            return resultModel;
        }
        resultModel.setCode(200);
        resultModel.setMessage("OK");
        return resultModel;
    }


    /**
     * 校验验证码是否正确
     * <p>
     * 负责人：史利挺
     *
     * @param phone 手机号
     * @param code  验证码
     * @param type  场景
     * @return
     * @throws Exception
     */
    @RequestMapping("/checkCode")
    @ResponseBody
    public ResultModel checkCode(String phone, String code, Integer type, Long customerId) throws Exception {
        log.info("phone:" + phone + "code" + code + "type" + type + "customerId" + customerId);
        ResultModel resultModel = new ResultModel();
        NoteModel noteModel = new NoteModel();
        noteModel.setPhone(phone);
        noteModel.setCode(code);
        noteModel.setMerchantId(customerId);
        noteModel.setDate(new Date());
        noteModel.setType(EnumHelper.getEnumType(VerificationType.class, type));
        boolean isTrue = verificationService.verifyCode(noteModel);
        if (!isTrue) {
            //验证码错误
            resultModel.setCode(502);
            resultModel.setMessage("验证码错误！");
            return resultModel;
        }
        resultModel.setCode(200);
        resultModel.setMessage("OK");
        return resultModel;
    }


    /**
     * 负责人：史利挺
     * 进入邀请开店的页面,判断逻辑如下：
     * 1.是否存在商户ID，如果没有则抛出异常
     * 2.是否有当前用户，没有则抛出异常
     * 3.商家是否启用店中店，没有则抛出异常
     * 4.判断用户是否已经开启店中店，如果是的则直接进入店中店首页
     *
     * @param gduid      邀请人ID，为空则是公开制
     * @param model
     * @param customerId 商家ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showOpenShop")
    public String showOpenShop(Long customerId, Long gduid, Model model) throws Exception {
        if (environment.acceptsProfiles("develop")) {
            customerId = 4471L;
        }
        if (customerId == null) {
            throw new CustomerNotFoundException("未获取到商户ID");
        }
        PublicParameterModel ppm = PublicParameterHolder.get();
        Long userId = ppm.getUserId();
        log.info("userID" + userId + "into showOpenShop");
        if (userId == null) {
            throw new UserNotFoundException("用户不存在");
        }
        User user = userRepository.findOne(userId);
        if (Objects.isNull(user)) {
            throw new UserNotFoundException("用户不存在");
        }
        SisConfig sisConfig = sisConfigRepository.findByMerchantId(customerId);
        if (Objects.isNull(sisConfig) || sisConfig.getEnabled() == 0) {
            throw new CustomerNotUseSisException("商家未启用店中店");
        }
        Sis sis = sisRepository.findByUser(user);
        if (!Objects.isNull(sis)) {
            //进入店中店首页
            model.addAttribute("customerId", customerId);
            return "redirect:getSisCenter"; //todo 店中店首页
        }
        //todo
        SisInviteLog sisInviteLog = new SisInviteLog();
//        if (sisConfig.getOpenMode() == 1) {//购买商品开店
//            if (sisConfig.getOpenNeedInvite() == 1) {//邀请制
//                if (gduid == null) {//没有邀请人
//                    Long members = sisQualifiedMemberRepository.countByMemberId(userId);
//                    if (members <= 0) {//未被授予开店资格
//                        throw new UserNotOpenShopQualificationException("未被授予开店资格");
//                    }
//
//                } else {//有邀请人
//                    User guser = userRepository.findOne(gduid);
//                    if (Objects.isNull(guser)) {
//                        throw new UserNotFoundException("无法获取你的上级");
//                    }
//                    if (UserType.normal.equals(guser.getUserType())) {//邀请人是会员
//                        throw new UserNotOpenShopQualificationException("邀请你的上级不是小伙伴，无法注册开店");
//                    }
//                    sisInviteLog.setInviterId(gduid);
//                    model.addAttribute("inviterName", guser.getWxNickName());
//                }
//            } else {    //公开制
////                if (UserType.normal.equals(user.getUserType())) {
////                    throw new UserNotOpenShopQualificationException("请升级为小伙伴之后开店");
////                }
//            }
////            Long invites = sisInviteRepository.countByAcceptIdAndInviterId(userId, gduid);
////            if (invites > 0) {//已经填写过邀请信息
////                model.addAttribute("customerId",customerId);
////                return "redirect:showOpenShopGoodsDetail";
////            }
//
//        } else {//免费开店
//            if (UserType.normal.equals(user.getUserType())) {
//                throw new UserNotOpenShopQualificationException("请升级为小伙伴之后开店");
//            }
//            Long invites = sisInviteRepository.countByAcceptIdAndInviterId(userId, gduid);
//            if (invites > 0) {//已经填写过邀请信息
//                model.addAttribute("customerId", customerId);
//                return "redirect:getSisCenter";//表示已经开店了
//            }
//        }

        Integer openNeedInvite=sisConfig.getOpenNeedInvite()==null?-1:sisConfig.getOpenNeedInvite();
        switch (openNeedInvite){
            case 1://邀请制
                if (gduid == null) {//没有邀请人
                    Long members = sisQualifiedMemberRepository.countByMemberId(userId);
                    if (members <= 0) {//未被授予开店资格
                        throw new UserNotOpenShopQualificationException("未被授予开店资格");
                    }
                } else {//有邀请人
                    User guser = userRepository.findOne(gduid);
                    if (Objects.isNull(guser)) {
                        throw new UserNotFoundException("无法获取邀请人的信息");
                    }
                    if (UserType.normal.equals(guser.getUserType())) {//邀请人是会员
                        throw new UserNotOpenShopQualificationException("邀请你的人不是小伙伴，无法注册开店");
                    }
                    sisInviteLog.setInviterId(gduid);
                    String name=guser.getWxNickName()==null?guser.getLoginName():guser.getWxNickName();
                    model.addAttribute("inviterName",name);
                }
                break;
            case 0://公开制
                break;
            default:
                throw new SisException("未知的开店模式");
        }

        Integer openMode=sisConfig.getOpenMode()==null?-1:sisConfig.getOpenMode();
        switch (openMode){
            case 1:
                break;
            case 0:
                if (UserType.normal.equals(user.getUserType())) {
                    throw new UserNotOpenShopQualificationException("请升级为小伙伴之后开店");
                }
                break;
            default:
                throw new SisException("未知的开店门槛");
        }

        model.addAttribute("sisInviteLog", sisInviteLog);
        model.addAttribute("customerId", customerId);
        model.addAttribute("free", sisConfig.getOpenMode());
        model.addAttribute("openGoodsMode", sisConfig.getOpenGoodsMode());//todo 开店商品模式修改


        if (sisConfig.getOpenGoodsMode() == 1 && sisConfig.getOpenGoodsIdlist() != null) {//todo 开店商品模式修改
            OpenGoodsIdLevelIds openGoodsIdLevelIds = sisConfig.getOpenGoodsIdlist();
            List<SisLevelModel> sisLevelModels = new ArrayList<>();
            List<SisLevel> sisLevels = sisLevelRepository.findByMerchantId(customerId);
            if (sisLevels != null) {
                for (SisLevel l : sisLevels) {
                    SisLevelModel sisLevelModel = new SisLevelModel();
                    for (OpenGoodsIdLevelId o : openGoodsIdLevelIds.values()) {
                        if (l.getId().equals(o.getLevelid())) {
                            Goods goods=null;
                            if(o.getGoodsid()!=null){
                                goods = goodsRepository.findOne(o.getGoodsid());
                            }

                            if(goods!=null){
                                sisLevelModel.setLevelId(l.getId());
                                sisLevelModel.setLevelTitle(l.getLevelName());
                                sisLevelModel.setGoodsId(goods.getId());
                                sisLevelModel.setGoodsTitle(goods.getTitle());
                                sisLevelModel.setGoodsPrice(goods.getPrice());
                                sisLevelModels.add(sisLevelModel);
                            }
                            break;
                        }

                    }
                }
            }

            model.addAttribute("openGoods", sisLevelModels);
        }
        if(!StringUtils.isEmpty(sisConfig.getSharePic())){
            String invitePic=sisConfig.getSharePic();
            model.addAttribute("invitePic",commonConfigService.getResourcesUri()+invitePic);
        }
        return "sisweb/openShop";


    }


    /**
     * 收集用户开店的信息
     * <p>
     * 负责人：史利挺
     *
     * @param sisInviteLog 邀请的实体
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/collectSisInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResultModel collectSisInfo(SisInviteLog sisInviteLog) throws Exception {
        log.info("collectSisInfo" + sisInviteLog.getCustomerId() + " " + sisInviteLog.getRealName() + " " + sisInviteLog.getMobile());
        ResultModel resultModel = new ResultModel();
        if (sisInviteLog.getCustomerId() == null || sisInviteLog.getMobile() == null || sisInviteLog.getRealName() == null) {
            resultModel.setCode(500);
            resultModel.setMessage("参数出错，无法收集邀请数据");
            return resultModel;
        }

        PublicParameterModel ppm = PublicParameterHolder.get();
        Long userId = ppm.getUserId();

        sisInviteLog.setAcceptId(userId);
        sisInviteLog.setAcceptTime(new Date());
        sisInviteRepository.save(sisInviteLog);
        resultModel.setCode(200);
        resultModel.setMessage("OK");
        return resultModel;
//        //判断是否有门槛
//        SisConfig sisConfig = sisConfigRepository.findByMerchantId(customerId);
//        if (sisConfig.getOpenMode() == 0) {//没有门槛
//            User user = userRepository.findOne(userId);
//            //开店
//            userService.newOpen(user);
//            log.debug(userId + "开店成功！");
//            model.addAttribute("customerId",customerId);
//            return "redirect:getSisCenter";//表示已经开店了
//        }
//        model.addAttribute("customerId",customerId);
//        return "redirect:showOpenShopGoodsDetail";
    }

    /**
     * 免费开店
     *
     * @param customerId 商家ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/openFreeShop", method = RequestMethod.POST)
    @ResponseBody
    public ResultModel openFreeShop(Long customerId) throws Exception {
        ResultModel resultModel = new ResultModel();
        if (environment.acceptsProfiles("develop")) {
            customerId = 4471L;
        }
        if (customerId == null) {
            resultModel.setCode(500);
            resultModel.setMessage("未获取到商户ID");
            return resultModel;
        }
        PublicParameterModel ppm = PublicParameterHolder.get();
        Long userId = ppm.getUserId();
        User user = userRepository.findOne(userId);
        if (user == null) {
            resultModel.setCode(500);
            resultModel.setMessage("未找到该用户");
            return resultModel;
        }
        SisConfig sisConfig = sisConfigRepository.findByMerchantId(customerId);
        if (sisConfig == null || sisConfig.getEnabled() == 0) {
            resultModel.setCode(500);
            resultModel.setMessage("商家配置未启用");
            return resultModel;
        }
        if (sisConfig.getOpenMode() != 0) {
            resultModel.setCode(500);
            resultModel.setMessage("商家未配置免费开店");
            return resultModel;
        }
        Sis sis = sisRepository.findByUser(user);
        if (sis != null) {
            resultModel.setCode(500);
            resultModel.setMessage(userId + "店中店已经开启");
            return resultModel;
        }
        userService.newOpen(user, null, sisConfig);
        resultModel.setCode(200);
        resultModel.setMessage("OK");
        return resultModel;
    }


    /**
     * 进入购买开店商品的页面
     * 负责人：史利挺
     *
     * @param customerId 商家ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "showOpenShopGoodsDetail", method = RequestMethod.GET)
    public String showOpenShopGoodsDetail(Long customerId, Model model, Long goodsId) throws Exception {
        if (environment.acceptsProfiles("develop")) {
            customerId = 4471L;
        }
        if (customerId == null) {
            throw new CustomerNotFoundException("未获取到商户ID");
        }
        SisConfig sisConfig = sisConfigRepository.findByMerchantId(customerId);
        if (Objects.isNull(sisConfig) || sisConfig.getEnabled() == 0) {
            throw new CustomerNotUseSisException("商家未启用店中店");
        }
        Long openGoodsId;
        if (sisConfig.getOpenGoodsMode() == 1) {//todo 开店商品模式修改
            openGoodsId = goodsId;
        } else {
            openGoodsId = sisConfig.getOpenGoodsId();
        }
        Goods goods = goodsRepository.findOne(openGoodsId);
        if (Objects.isNull(goods)) {
            throw new GoodsNotFoundException("开店商品不存在");
        }

        ProductSpecifications productSpecifications = goods.getSpecificationsCache();
        if (Objects.isNull(productSpecifications) || productSpecifications.isEmpty()) {
            throw new ProductNotFoundException("开店货品不存在");
        }
        Long productId = null;
        for (Long key : productSpecifications.keySet()) {
            productId = key;
            break;
        }
        if (Objects.isNull(productId)) {
            throw new ProductNotFoundException("开店货品不存在");
        }

        goods.setSmallPic(new MallEmbedResource(commonConfigService.getMallApiWebUrl() + goods.getSmallPic().getValue()));
        int freeze = 0;
        Set<Product> products = goods.getProducts();
        if (products != null) {
            for (Product p : products) {
                freeze = freeze + p.getFreeze();
            }
        }
        goods.setStock(goods.getStock() - freeze);
        model.addAttribute("good", goods);
        String url = getMerchantSubDomain(customerId) + "/mall/SubmitOrder.aspx?" +
                "fastbuy=1&" +
                "traitems=" + goods.getId() + "_" + productId + "_1&" +
                "customerid=" + customerId + "&" +
                "showwxpaytitle=1" + "&" + "returl=/UserCenter/ShopInShop/OpenSuccess.aspx%3Fcustomerid%3D" + customerId;
        model.addAttribute("goodsUrl", url);
        model.addAttribute("customerId", customerId);
        String resUrl = commonConfigService.getResourceServerUrl();
        model.addAttribute("goodsDesc", goods.getIntro().replaceAll("src=\"/resource/", "  width='100%' src=\"" + resUrl + "/resource/"));

        return "sisweb/openShopGoodsDetail";

    }

    /**
     * 进入分享开店邀请页面
     * 负责人：史利挺
     *
     * @param customerId 商家ID
     * @param request    HttpServletRequest请求
     * @param model      返回的model
     * @return 视图
     * @throws Exception
     */
    @RequestMapping(value = "inviteOpenShop", method = RequestMethod.GET)
    public String inviteOpenShop(Long customerId, HttpServletRequest request, Model model) throws Exception {
        if (environment.acceptsProfiles("develop")) {
            customerId = 4471L;
        }
        if (customerId == null) {
            throw new CustomerNotFoundException("未获取到商户ID");
        }
        SisConfig sisConfig = sisConfigRepository.findByMerchantId(customerId);
        if (Objects.isNull(sisConfig) || sisConfig.getEnabled() == 0) {
            throw new CustomerNotUseSisException("商家未启用店中店");
        }
        PublicParameterModel ppm = PublicParameterHolder.get();
        Long userId = ppm.getUserId();
        if (userId == null) {
            throw new UserNotFoundException("用户不存在");
        }
        User user = userRepository.findOne(userId);
        if (user == null) {
            throw new UserNotFoundException("用户不存在");
        }
        String port = "";
        if (request.getServerPort() != 80) {
            port = ":" + request.getServerPort();

        }
        MerchantConfig merchantConfig = merchantConfigRepository.findByMerchantId(customerId);
        String shareUrl = "http://" + request.getServerName() + port + request.getContextPath() +
                "/sisweb/showOpenShop?gduid=" + userId + "&customerId=" + customerId;
        model.addAttribute("userId", userId);
        model.addAttribute("customerId", customerId);
        model.addAttribute("shareTitle", convertShareContent(sisConfig.getShareTitle(), customerId, userId));
        model.addAttribute("shareDesc", convertShareContent(sisConfig.getShareDesc(), customerId, userId));
        String sharePic = "";
        if (merchantConfig != null) {
            sharePic = commonConfigService.getResourceServerUrl() + merchantConfig.getLogoImg();
        }
        model.addAttribute("sharePic", sharePic);
        if(!StringUtils.isEmpty(sisConfig.getSharePic())){
            String invitePic=sisConfig.getSharePic();
            model.addAttribute("invitePic",commonConfigService.getResourcesUri()+invitePic);
        }
        model.addAttribute("customerUrl", getMerchantSubDomain(customerId));
        model.addAttribute("shareUrl", shareUrl);
        return "sisweb/inviteOpenShop";

    }

    /**
     * 转换分享内容
     * 负责人史利挺
     *
     * @param content    替换内容
     * @param customerId 商户ID
     * @param userId     用户ID
     * @return
     */
    private String convertShareContent(String content, Long customerId, Long userId) {
        String merchantNickName = merchantRepository.findNickNameByMerchantId(customerId);
        merchantNickName = MathHelper.filterSpecialCharacter(merchantNickName);

        String userWxNickName = userRepository.findWxNickNameById(userId);
        userWxNickName = MathHelper.filterSpecialCharacter(userWxNickName);

        content = content.replaceAll("\\[name\\]", userWxNickName);
        content = content.replaceAll("\\[mgt\\]", merchantNickName);
        return content;
    }

    /**
     * 获取商户的网址
     *
     * @param merchantId 商户ID
     * @return
     */
    private String getMerchantSubDomain(Long merchantId) {
        String subDomain = merchantRepository.findSubDomainByMerchantId(merchantId);
        if (subDomain == null) {
            subDomain = "";
        }
        return "http://" + subDomain + "." + commonConfigService.getMallDomain();
    }


    /**
     * app登录(弃用)
     *
     * @param request request请求
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/appLogin", method = {RequestMethod.GET, RequestMethod.POST})
    public String appLogin(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        log.info("into appLogin");
        String sign = request.getParameter("sign");
        if (sign == null || !sign.equals(securityService.getSign(request))) {
            throw new SisException("授权失败，签名未通过！");
        }

        String userId = request.getParameter("userid");
        if (StringUtils.isEmpty(userId)) {
            throw new UserNotFoundException("参数错误：没有用户ID!");
        }
        String customerId = request.getParameter("customerid");
        if (StringUtils.isEmpty(customerId)) {
            throw new CustomerNotFoundException("参数错误：没有商户ID！");
        }
        //设置cookie
        userService.setLoginType(request, response, "1");
        userService.setUserId(Long.parseLong(userId), request, response);
        model.addAttribute("customerId", customerId);
        return "redirect:showOpenShop";

    }

    /**
     * 进入店铺升级页面
     * @param customerId
     * @param model
     * @return
     * @throws CustomerNotFoundException
     * @throws SisException
     */
    @RequestMapping(value = "/sisLevelUpgrade", method = RequestMethod.GET)
    public String sisLevelUpdate(Long customerId, Model model) throws CustomerNotFoundException, SisException {
        if (Objects.isNull(customerId)) {
            throw new CustomerNotFoundException("未找到该商户");
        }
        PublicParameterModel ppm = PublicParameterHolder.get();
        Long userId = ppm.getUserId();
        User user = userRepository.findOne(userId);
        if (Objects.isNull(user)) {
            throw new SisException("用户不存在或者已经过期");
        }
        Sis sis = sisRepository.findByUser(user);
        if (Objects.isNull(sis)) {
            throw new SisException("您尚未开店");
        }
        if (Objects.isNull(sis.getSisLevel()) || Objects.isNull(sis.getSisLevel().getLevelNo())) {
            throw new SisException("您店铺没有店铺等级");
        }

        List<SisLevel> sisLevels = sisLevelService.getListBelongByLevelId(sis.getSisLevel().getLevelNo(), customerId);
//        sisLevels.stream().forEach(sisLevel -> {
//            SisLevelUpgradeModel sisLevelUpgradeModel = new SisLevelUpgradeModel();
//        });
        SisConfig sisConfig = sisConfigRepository.findByMerchantId(customerId);

        if(Objects.isNull(sisConfig)||sisConfig.getOpenGoodsMode()==null||sisConfig.getOpenGoodsMode()==0){//todo 开店商品模式修改
            throw new SisException("店铺配置尚未初始化");
        }

        if (Objects.isNull(sisConfig) || Objects.isNull(sisConfig.getOpenGoodsIdlist()) || Objects.isNull(sisConfig.getExtraUpGoodsId())) {
            throw new SisException("店铺配置尚未初始化");
        }
        Goods extraGoods = goodsRepository.findOne(sisConfig.getExtraUpGoodsId());
        if (Objects.isNull(extraGoods) || Objects.isNull(extraGoods.getSpecificationsCache())
                || extraGoods.getSpecificationsCache().size() == 0)
            throw new SisException("补差额商品不存在，无法进行升级");


//        String url = getMerchantSubDomain(customerId) + "/mall/SubmitOrder.aspx?" +
//                "fastbuy=1&" +
//                "traitems=" + goods.getId() + "_" + productId + "_1&" +
//                "customerid=" + customerId + "&" +
//                "showwxpaytitle=1" + "&" + "returl=/UserCenter/ShopInShop/OpenSuccess.aspx%3Fcustomerid%3D" + customerId;
        Long productId = extraGoods.getSpecificationsCache().entrySet()
                .stream().mapToLong(value -> value.getValue().getProductId()).findAny().getAsLong();
        String url = getMerchantSubDomain(customerId) + "/mall/SubmitOrder.aspx?" +
                "fastbuy=1&" + "&" +
                "customerid=" + customerId + "&" +
                "showwxpaytitle=1" + "&" + "returl=/UserCenter/ShopInShop/OpenSuccess.aspx%3Fcustomerid%3D" + customerId +
                "&traitems=" + extraGoods.getId() + "_" + productId;

        double originalPrice = sisConfig.getOpenGoodsIdlist().entrySet().stream()
                .filter(entry -> entry.getValue().getLevelid().equals(sis.getSisLevel().getId()))
                .mapToDouble(value
                        -> goodsRepository.findOne(value.getValue().getGoodsid()).getPrice()).findAny().getAsDouble();
        List<SisLevelUpgradeModel> models = new ArrayList<>();
        log.info("sisLevels的长度是"+sisLevels.size());
        log.info("openGoodsIdList的长度是"+sisConfig.getOpenGoodsIdlist().size());
        for (Map.Entry<Long, OpenGoodsIdLevelId> entry : sisConfig.getOpenGoodsIdlist().entrySet()) {
            sisLevels.stream().forEach(sisLevel -> {
                if (entry.getValue().getLevelid().equals(sisLevel.getId())) {
                    SisLevelUpgradeModel sisLevelUpgradeModel = new SisLevelUpgradeModel();
//                    sisLevelUpgradeModel.setImgUrl();
                    sisLevelUpgradeModel.setLevelId(sisLevel.getId());
                    sisLevelUpgradeModel.setLevelName(sisLevel.getLevelName());
                    sisLevelUpgradeModel.setLevelNo(sisLevel.getLevelNo());
//                    sisLevelUpgradeModel.setGoodsUrl();
                    Goods goods = goodsRepository.findOne(entry.getValue().getGoodsid());
                    if (Objects.nonNull(goods.getPrice())) {
                        //需要补的钱
                        Long price = Math.round(goods.getPrice() - originalPrice);
                        sisLevelUpgradeModel.setOriginalPrice(Math.round(goods.getPrice()));
                        sisLevelUpgradeModel.setCurrentPrice(price);
                        int goodsNumber=new Double(Math.ceil((goods.getPrice()-originalPrice)/extraGoods.getPrice())).intValue();
                        String goodsUrl = url + "_" + goodsNumber;
                        sisLevelUpgradeModel.setGoodsUrl(goodsUrl);
                        models.add(sisLevelUpgradeModel);
                    }
                }
            });
        }
//        models = models.stream().sorted().collect(Collectors.toList());

        List<SisOpenAwardAssign> sisOpenAwardAssigns=sisOpenAwardAssignRepository.findByMerchant_Id(customerId);
        for(SisOpenAwardAssign s:sisOpenAwardAssigns){
            for(SisLevelUpgradeModel su:models){
                if(s.getLevel().getId().equals(su.getLevelId())){
                    List<String> stringList=su.getTreatments();
                    if(stringList==null){
                        stringList=new ArrayList<>();
                    }
                    stringList.add("• 推荐一家"+s.getGuideLevel().getLevelName()+"获得"+Math.round(s.getAdvanceVal())+"元");
                    su.setTreatments(stringList);
                }
            }

        }
        Collections.sort(models, (t1, t2) -> t1.getLevelNo().compareTo(t2.getLevelNo()));
        //是否是最高等级
        SisLevel topSisLevel=sisLevelRepository.findFirstByMerchantIdOrderByLevelNoDesc(customerId);
        if(topSisLevel==null||topSisLevel.getId().equals(sis.getSisLevel().getId())){
            model.addAttribute("isTopSisLevel",true);
        }
        model.addAttribute("levels", models);
        model.addAttribute("customerId", customerId);
        model.addAttribute("sis", sis);
        return "/sisweb/sisLevelUpgrade";
    }

    /**
     * 切换铺货模式(目前有一键铺货和自主选货)
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/switchShelves")
    @ResponseBody
    public ResultModel switchShelves(Long customerId) throws Exception{
        ResultModel resultModel=new ResultModel();
        if (Objects.isNull(customerId)) {
            resultModel.setCode(500);
            resultModel.setMessage("未找到商家");
            return resultModel;
        }
        PublicParameterModel ppm = PublicParameterHolder.get();
        Long userId = ppm.getUserId();
        if(userId==null){
            resultModel.setCode(500);
            resultModel.setMessage("未找到用户ID");
            return resultModel;
        }
        User user =userRepository.findOne(userId);
        if(Objects.isNull(user)){
            resultModel.setCode(500);
            resultModel.setMessage("未找到用户");
            return resultModel;
        }
        Sis sis=sisRepository.findByUser(user);
        if(Objects.isNull(sis)){
            resultModel.setCode(500);
            resultModel.setMessage("未找到店铺");
            return resultModel;
        }
        Boolean shelvesAllGoods=sis.getShelvesAllGoods();
        if(shelvesAllGoods==null){
            shelvesAllGoods=false;
        }
        sis.setShelvesAllGoods(!shelvesAllGoods);
        sisRepository.save(sis);
        resultModel.setCode(200);
        resultModel.setMessage("OK");
        return resultModel;
    }
}
