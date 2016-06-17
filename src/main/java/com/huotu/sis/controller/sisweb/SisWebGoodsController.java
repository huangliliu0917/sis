package com.huotu.sis.controller.sisweb;

import com.huotu.huobanplus.common.UserType;
import com.huotu.huobanplus.common.dataService.AdvanceQuatoRebateService;
import com.huotu.huobanplus.common.dataService.NormalRebateService;
import com.huotu.huobanplus.common.dataService.UserTempIntegralHistoryService;
import com.huotu.huobanplus.common.entity.*;
import com.huotu.huobanplus.common.entity.support.*;
import com.huotu.huobanplus.common.model.RebateCompatible;
import com.huotu.huobanplus.common.model.RebateInfo;
import com.huotu.huobanplus.common.model.RebateMode;
import com.huotu.huobanplus.common.model.adrebateconfig.ProductDisRebateDesc;
import com.huotu.huobanplus.common.repository.*;
import com.huotu.huobanplus.common.utils.DateUtil;
import com.huotu.sis.common.MathHelper;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisGoods;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.entity.support.SisRebateTeamManagerSetting;
import com.huotu.sis.model.sisweb.*;
import com.huotu.sis.repository.*;
import com.huotu.sis.service.*;
import com.huotu.sis.common.PublicParameterHolder;
import com.huotu.sis.exception.SisException;
import com.huotu.sis.exception.UserNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by lgh on 2015/12/29.
 */

@Controller
@RequestMapping("/sisweb")
public class SisWebGoodsController {

    private static String img_path = "images/g1.png";
    private static Log logger = LogFactory.getLog(SisWebGoodsController.class);
    @Autowired
    SisGoodsRepository sisGoodsRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MerchantRepository merchantRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    SisOrderItemsRepository sisOrderItemsRepository;
    @Autowired
    SisOrderRepository sisOrderRepository;
    @Autowired
    CommonConfigsService commonConfigService;
    @Autowired
    UTIHRepository utihRepository;
    @Autowired
    private UserLevelRepository userLevelRepository;
    @Autowired
    private MerchantConfigRepository merchantConfigRepository;
    @Autowired
    private AdvanceQuatoRebateService advanceQuatoRebateService;
    @Autowired
    private SisQualifiedMemberRepository sisQualifiedMemberRepository;
    @Autowired
    private NormalRebateService normalRebateService;
    @Autowired
    private SqlService sqlService;
    private Log log = LogFactory.getLog(SisWebGoodsController.class);
    @Autowired
    private SisGoodsService sisGoodsService;
    @Autowired
    private UserTempIntegralHistoryService userTempIntegralHistoryService;
    @Autowired
    private SisRepository sisRepository;
    @Autowired
    private SisConfigRepository sisConfigRepository;
    @Autowired
    private SisLevelRepository sisLevelRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private CommonConfigsService commonConfigsService;
    @Autowired
    private SisGoodsRecommendService sisGoodsRecommendService;

    @Autowired
    private SisService sisService;

    /**
     * 查找品牌对应的商品列表详情
     *
     * @param page
     * @param pageSize
     * @param brandId
     * @return
     * @throws IOException
     * @throws SisException
     */
    @RequestMapping(value = "/getGoodsListByBrandId", method = RequestMethod.POST)
    @ResponseBody
    public PageGoodsModel getGoodsListByBrandId(int page, int pageSize, Long brandId, Long customerId) throws IOException, SisException {
        Long userId = getCurrentUserId();
        User user = userRepository.findOne(userId);
        if (null == user) {
            throw new SisException("用户不存在或已过期");
        }
        Brand brand = null;
        if (null != brandId) {
            brand = brandRepository.findOne(brandId);
        }
        if (null == brand) {
            throw new SisException("无法查询到该品牌");
        }
        Page<Goods> goodsList = sisGoodsService.getAllGoodsByCustomerIdAndTitleAndCatPath(customerId,
                null, null, brandId, null, page, pageSize);

        List<SisGoods> sisGoodsList = new ArrayList<>();
        int count = 0;
        if (goodsList != null && goodsList.getContent() != null) {
            sisGoodsList = getGoodsListForSelected(goodsList.getContent(), user);
            count = (int) goodsList.getTotalElements();
        }
        //计算直推返利/分销返利值
        List<PcSisGoodsModel> list = calculateValue(sisGoodsList, user);
        //
        PageGoodsModel model = new PageGoodsModel();
        int pageCount = count / pageSize + 1;
        model.setRows(list);
        model.setPageCount(pageCount);
        model.setPageSize(pageSize);
        model.setPageIndex(page);
        model.setTotal(count);

        return model;
    }

    private List<SisGoods> getGoodsListForSelected(List<Goods> goodsList, User user) {
        List<SisGoods> sisGoodsList = new ArrayList<>();
        goodsList.stream().forEach(p -> {
            List<SisGoods> sisGoodsListTemp = sisGoodsService.getAllSisGoodsList(p.getId(), user.getId(), user.getMerchant().getId());
            if (sisGoodsListTemp != null && sisGoodsListTemp.size() == 1) {
                sisGoodsList.add(sisGoodsListTemp.get(0));
            } else {
                SisGoods sisGoods = new SisGoods();
                sisGoods.setGoods(p);
                sisGoods.setSelected(false);    //设置成未上架
                sisGoodsList.add(sisGoods);
            }
        });
        return sisGoodsList;
    }

    /**
     * 返回到店铺首页
     *
     * @param model 返回的model
     * @param model
     * @throws IOException  IO异常
     * @throws SisException 店中店异常
     * @throws IOException  IO异常，SisException 店中店异常
     */
    @RequestMapping(value = "/sisIndex", method = RequestMethod.GET)
    public String sisIndex(Long customerId,Model model) throws IOException, SisException {
        Long userId = getCurrentUserId();
        User user = userRepository.findOne(userId);
        if (null == user)
            throw new SisException("该用户不存在或者已经过期");
        Sis sis = sisRepository.findByUser(user);
        if (null == sis)
            throw new SisException("您尚未开店，请先去开店再进行下一步操作！");
        if (null == user.getWeixinImageUrl())
            sis.setImgPath("images/moren.png");
        else
            sis.setImgPath(user.getWeixinImageUrl());
        model.addAttribute("sis", sis);
        SisLevel sisLevel = sis.getSisLevel();
        if (null != sisLevel) {
            model.addAttribute("sisLevelName", sisLevel.getLevelName());
        } else {
            model.addAttribute("sisLevelName", "默认等级");
        }
        model.addAttribute("customerId", customerId);
        return "/sisweb/sisIndex";
    }

    /**
     * 店铺链接地址
     *
     * @param merchantId
     * @param userId
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
        return "http://" + subDomain + "." + commonConfigsService.getMallDomain();
    }

    /**
     * 查找店中店商品详情
     *
     * @param goodId  物品id
     * @param request 前台请求
     * @throws IOException
     */
    @RequestMapping(value = "/getSisGoodsDetail", method = RequestMethod.GET)
    public String getSisGoodsDetail(Long goodId,Long customerId, HttpServletRequest request) throws IOException, SisException {
        Long userId = getCurrentUserId();
        User user = userRepository.findOne(userId);
        Sis sis=sisRepository.findByUser(user);

        GoodsDetailModel goodsDetailModel = new GoodsDetailModel();
        Goods goods = goodsRepository.findOne(goodId);
//        Sis sis = sisRepository.findByUser(user);
//        String goodsShareUrl = getSisGoodsUrl(user.getMerchant().getId(), user.getId())+goodId;
//        if(null==sis){
//            response.sendRedirect(goodsShareUrl);
//        }
        SisGoods sisGoods = sisGoodsRepository.findByGoodsAndUser(goods, user);

        if(null==sisGoods){
            sisGoods = new SisGoods();
            sisGoods.setGoods(goods);
            sisGoods.setDeleted(false);
            sisGoods.setSelected(false);
            sisGoods.setUser(user);
            sisGoods.setMerchant(user.getMerchant());
        }
        goodsDetailModel.setTitle(goods.getTitle());

        if (goods.getPricesCache() != null && goods.getPricesCache().size() > 0) {
            for (Map.Entry<Long, LevelPrice> entry : goods.getPricesCache().entrySet()) {
                goodsDetailModel.setPrice(entry.getValue().getMinPrice());
            }
        } else {
            goodsDetailModel.setPrice(goods.getPrice());
        }

        List<SisGoods> sisGoodsList = new ArrayList<>();
        sisGoodsList.add(sisGoods);

        List<PcSisGoodsModel> pcSisGoodsModels = calculateValue(sisGoodsList, user);
        if (null != pcSisGoodsModels && pcSisGoodsModels.size() == 1) {
            goodsDetailModel.setPrice(pcSisGoodsModels.get(0).getPrice());
            goodsDetailModel.setDirectRebate(pcSisGoodsModels.get(0).getDirectRebate());
            goodsDetailModel.setMinRebate(pcSisGoodsModels.get(0).getMinRebate());
            goodsDetailModel.setMaxRebate(pcSisGoodsModels.get(0).getMaxRebate());
        }

        goodsDetailModel.setStore(goods.getStock()); //-1是无限制
        String resUrl = commonConfigService.getResoureServerUrl();
        goodsDetailModel.setDetail(goods.getIntro().replaceAll("src=\"/resource/", "src=\"" + resUrl + "/resource/"));
        goodsDetailModel.setGoodsId(goodId);
        if (sisGoods != null && sisGoods.isSelected() == true) {
            goodsDetailModel.setIsUp(0);
        } else {
            goodsDetailModel.setIsUp(1);
        }
        StringBuilder picture = new StringBuilder();
        picture.append(commonConfigService.getResoureServerUrl() + goods.getSmallPic().getValue());
        goodsDetailModel.setPicture(picture.toString());
        goodsDetailModel.setShelves(sis.getShelvesAllGoods()==null?false:sis.getShelvesAllGoods());
        request.setAttribute("customerId", customerId);
        request.setAttribute("goodsDetailModel", goodsDetailModel);
        return "sisweb/goodsDetail";
    }

    /**
     * 返回到选取商品页面
     *
     * @param model
     * @throws IOException
     * @throws SisException
     */
    @RequestMapping(value = "/goodsIndex", method = RequestMethod.GET)
    public String goodsIndex(Long customerId, Model model, String pageType, HttpServletRequest request) throws IOException, SisException {
        Long userId = getCurrentUserId();
        User user = userRepository.findOne(userId);
        if (null == user) {
            throw new SisException("该用户不存在或者已经过期");
        }
        Long count = sisGoodsService.countByUserId(customerId,userId);
        Sis sis =sisRepository.findByUser(user);
        request.setAttribute("pageType", pageType);
        model.addAttribute("user", user);
        model.addAttribute("customerId", customerId);
        model.addAttribute("count", count);
        if(sis!=null){
            model.addAttribute("shelvesModel",sis.getShelvesAllGoods()==null?false:sis.getShelvesAllGoods());
        }
        return "/sisweb/sisGoodsList";
    }

    /**
     * 店铺选择的商品,未完善
     *
     * @param page
     * @param pageSize
     * @return
     * @throws IOException
     * @throws SisException
     */
    @RequestMapping(value = "/getSisGoodsList", method = RequestMethod.POST)
    @ResponseBody
    public PageGoodsModel getSisGoodsListSelected(int page, int pageSize) throws IOException, SisException {
        Long userId = getCurrentUserId();
        User user = userRepository.findOne(userId);
        if (null == user) {
            throw new SisException("用户不存在或者已过期");
        }
        logger.debug("show sis goods list begin");
        Sis sis=sisRepository.findByUser(user);
        Page<SisGoods> pages;
        if(sis==null||sis.getShelvesAllGoods()==null||!sis.getShelvesAllGoods()){
            pages = sisGoodsService.getSisGoodsList(user.getMerchant().getId(), user.getId(), page - 1, pageSize);
        }else{
            pages = sisGoodsService.getMallGoods(user.getMerchant(), user, page - 1, pageSize);
        }
        List<SisGoods> goodsList = pages.getContent();
        //计算直推返利/分销返利值
        List<PcSisGoodsModel> list = calculateValue(goodsList, user);
        logger.debug("show sis goods list size " + list.size());
        PageGoodsModel model = new PageGoodsModel();
        model.setRows(list);
        Long count = pages.getTotalElements();
        int pageCount = Integer.parseInt(count.toString()) / pageSize + 1;
        model.setPageCount(pageCount);
        model.setPageSize(pageSize);
        model.setPageIndex(page);
        model.setTotal(Integer.parseInt(count.toString()));
        logger.debug("show sis goods list end ");
        return model;
    }

    /**
     * 查找某个商户的商城上架的普通商品
     *
     * @param page
     * @param pageSize
     * @param keywords
     * @param categoryId
     * @param sortOption 0:直推返利
     *                   1：商品人气
     *                   2:上架时间
     * @throws IOException
     * @throws SisException
     */
    @RequestMapping(value = "/getGoodsList", method = RequestMethod.POST)
    @ResponseBody
    public PageGoodsModel getGoodsList(int page, int pageSize,
                                       @RequestParam(required = false) String keywords,
                                       @RequestParam(required = false) Long categoryId,
                                       @RequestParam(required = false) Integer sortOption) throws IOException, SisException {
        Long userId = getCurrentUserId();
        User user;
        if (userId != null) {
            user = userRepository.findOne(userId);
            if (user == null) {
                throw new SisException("用户不存在或已过期");
            }
        } else {
            throw new SisException("用户不存在或已过期");
        }
        Page<Goods> goodsList = sisGoodsService.getAllGoodsByCustomerIdAndTitleAndCatPath(user.getMerchant().getId(),
                keywords, categoryId, null, sortOption, page, pageSize);

        List<SisGoods> sisGoodsList = new ArrayList<>();
        if (goodsList != null && goodsList.getContent() != null) {
            sisGoodsList = getGoodsListForSelected(goodsList.getContent(), user);
        }
        //计算直推返利/分销返利值
        List<PcSisGoodsModel> list = calculateValue(sisGoodsList, user);
        //
        PageGoodsModel model = new PageGoodsModel();
        int count = (int) goodsList.getTotalElements();
        int pageCount = count / pageSize + 1;
        model.setRows(list);
        model.setPageCount(pageCount);
        model.setPageSize(pageSize);
        model.setPageIndex(page);
        model.setTotal(count);

        return model;
    }

    /**
     * 操作店中店商品
     * <b>负责人：史利挺</b>
     * 具体操作：
     * 上架：对已下架的商家进行上架
     * 下架：对已上架的商品进行下架
     * 删除：对商品进行删除
     * 置顶: 对商品进行置顶
     *
     * @param goodsId  商品ID
     * @param operType 对于商品的操作，包括 下架：0 ,上架：1，置顶：2，删除：3
     */
    @RequestMapping(value = "/operGoods", method = RequestMethod.POST)
    @ResponseBody
    public ModelMap operGoods(Long goodsId, Integer operType) throws Exception {
        Long userId = getCurrentUserId();
        Goods goods = goodsRepository.findOne(goodsId);
        ModelMap modelMap = new ModelMap();
        if (goods == null) {
            modelMap.addAttribute("success", Boolean.FALSE);
            modelMap.addAttribute("msg", "商品已下架或者已被删除");
            return modelMap;
        }
        User user = userRepository.findOne(userId);
        if (user == null) {
            modelMap.addAttribute("success", Boolean.FALSE);
            modelMap.addAttribute("msg", "用户不存在或已过期");
            return modelMap;
        }
        Long customerId = user.getMerchant().getId();


        if (operType == 0) {
            List<SisGoods> sisGoodsList = sisGoodsService.getAllSisGoodsList(goodsId,userId,customerId);
            if (null == sisGoodsList || sisGoodsList.size()==0) {
                modelMap.addAttribute("success", Boolean.FALSE);
                modelMap.addAttribute("msg", "商品已下架或已被删除");
                return modelMap;
            }
            //商品人气减1
            if (goods.getMoods() != null && goods.getMoods() > 0) {
                goods.setMoods(goods.getMoods() - sisGoodsList.size());
                goodsRepository.save(goods);
            }
            sisGoodsList.stream().forEach(sisGoods -> sisGoodsRepository.delete(sisGoods));
            modelMap.addAttribute("success", Boolean.TRUE);
            return modelMap;

        } else if (operType == 1) {
            SisGoods sisGoods = sisGoodsRepository.findByGoodsAndUser(goods, user);
            if (null != sisGoods) {
                modelMap.addAttribute("success", Boolean.FALSE);
                modelMap.addAttribute("msg", "商品已上架，请检查");
                return modelMap;
            }
            Long count = sisGoodsService.countByUserId(customerId, userId);
            SisConfig sisConfig = sisConfigRepository.findByMerchantId(customerId);

            if (null != sisConfig && null != sisConfig.getMaxMartketableNum() &&

                    (Integer.parseInt(count.toString()) < sisConfig.getMaxMartketableNum()||sisConfig.getMaxMartketableNum()==0)) {

                sisGoods = new SisGoods();
                sisGoods.setMerchant(user.getMerchant());
                sisGoods.setDeleted(false);
                sisGoods.setSelected(true);
                sisGoods.setGoods(goods);
                sisGoods.setUser(user);
                sisGoodsRepository.save(sisGoods);
                //商品人气增1
                if (goods.getMoods() == null) {
                    goods.setMoods(1);
                } else {
                    goods.setMoods(goods.getMoods() + 1);
                }
                goodsRepository.save(goods);
                modelMap.addAttribute("success", Boolean.TRUE);
                return modelMap;
            } else {
                modelMap.addAttribute("msg", "您上架的商品数量已达上限,请删除已上架商品后再添加");
                modelMap.addAttribute("success", Boolean.FALSE);
                return modelMap;
            }

        } else if (operType == 2) {
            SisGoods sisGoods = sisGoodsRepository.findByGoodsAndUser(goods, user);
            if (null == sisGoods) {
                modelMap.addAttribute("success", Boolean.FALSE);
                modelMap.addAttribute("msg", "商品已下架或已被删除");
                return modelMap;
            }
            log.debug("topSisGoodsId" + sisGoodsRepository.getTopSisGoods(userRepository.findOne(userId)));
            sisGoods.setOrderWeight(sisGoodsRepository.getTopSisGoods(userRepository.findOne(userId)) + 1);
            sisGoodsRepository.save(sisGoods);
            modelMap.addAttribute("success", Boolean.TRUE);
            return modelMap;
        }
        modelMap.addAttribute("success", Boolean.FALSE);
        modelMap.addAttribute("msg", "操作失败");
        return modelMap;
    }

    /**
     * 店铺中心页面
     *
     * @param model
     * @return
     * @throws SisException
     * @throws IOException
     */
    @RequestMapping(value = "/getSisCenter", method = RequestMethod.GET)
    public String getSisCenter(Model model) throws SisException, IOException, UserNotFoundException {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new UserNotFoundException("用户不存在");
        }
        User user = userRepository.findOne(userId);
        if (null == user)
            throw new SisException("该用户不存在或者已经过期");

        SisConfig sisConfig = sisConfigRepository.findByMerchantId(user.getMerchant().getId());
        Sis sis = sisRepository.findByUser(user);
        if (Objects.isNull(sis)) {
            if (sisConfig.getOpenMode() == 0) {//小伙伴直接开启
                return "redirect:showOpenShop";

            } else if (sisConfig.getOpenMode() == 1) {//购买商品开启
                Long members = sisQualifiedMemberRepository.countByMemberId(userId);
                if (members > 0) {//被授予开店资格
                    return "redirect:showOpenShop";
                } else {//未被授予开店资格
                    throw new SisException("未被授予开店资格");
                }

            } else {
                throw new SisException("未知错误");
            }

        }

        if (Objects.isNull(user.getWeixinImageUrl())) {
            sis.setImgPath("images/moren.png");
        } else
            sis.setImgPath(user.getWeixinImageUrl());
        model.addAttribute("sis", sis);
        model.addAttribute("customerId", user.getMerchant().getId());
        SisLevel sisLevel = sis.getSisLevel();
        if (null != sisLevel) {
            model.addAttribute("sisLevelName", sisLevel.getLevelName());
        } else {
            model.addAttribute("sisLevelName", "默认等级");
        }
        //订单数量
//        Long orderCount = orderService.getCountByUserId(userId);
        Long orderCount = userTempIntegralHistoryService.getCountByUserId(userId,500);
        model.addAttribute("orderCount", orderCount);
        Date now = new Date();
        Date startDate = DateUtil.makeStartDate(now);
        Date endDate = DateUtil.makeEndDate(now);
        //今日返利
        List<UserTempIntegralHistory> historyList = userTempIntegralHistoryService.getListByUserIdAndDate(userId, 1, 500, startDate, endDate);
        int todayIntegrals = 0;
        for (UserTempIntegralHistory history : historyList) {
            todayIntegrals += history.getIntegral();
        }
        model.addAttribute("todayIntegrals", todayIntegrals);

        String indexUrl = null;
        try {
            indexUrl = getSisCustomerUrl(user.getMerchant().getId(), user.getId());
        } catch (IOException e) {
            logger.debug("error share url:" + e);
        }
        model.addAttribute("indexUrl", indexUrl+"&__newframe");

        //总收益
        List<UserTempIntegralHistory> list = userTempIntegralHistoryService.getListByUserIdAndDate(userId, 1, 500, null, null);
        int integrals = 0;
        for (UserTempIntegralHistory history : list) {
            integrals += history.getIntegral();
        }
        model.addAttribute("integrals", integrals);
        model.addAttribute("homePageColor",sisConfig.getHomePageColor());
        model.addAttribute("shareUrl","/sisweb/inviteOpenShop?customerId="+user.getMerchant().getId()+"&__newframe");

        return "/sisweb/sisCenter";
    }

    /**
     * 店铺订单页面
     *
     * @return
     * @throws SisException
     */
    @RequestMapping(value = "/orderIndex", method = RequestMethod.GET)
    public String goToSisOrderList(Long customerId,Model model) throws SisException {
        Long userId = getCurrentUserId();
        User user = userRepository.findOne(userId);
        if (null == user)
            throw new SisException("该用户不存在或者已经过期");
        model.addAttribute("customerId",customerId);
        return "/sisweb/sisOrderList";
    }

    /**
     * 店铺订单列表
     *
     * @param page
     * @param pageSize
     * @return
     * @throws SisException
     */
    @RequestMapping(value = "/getSisOrderList", method = RequestMethod.POST)
    @ResponseBody
    public PageOrderModel getSisOrderList(int page, int pageSize) throws SisException {
        Long userId = getCurrentUserId();
        User user = userRepository.findOne(userId);
        if (null == user)
            throw new SisException("用户不存在或已过期");
//        Sort sort = new Sort(Sort.Direction.DESC, "time");
//        Page<Order> pages = orderService.getPageByUserId(userId, page - 1, pageSize, sort);
//        PageOrderModel pageOrderModel = new PageOrderModel();
//        pageOrderModel.setRows(pages.getContent());
//        int pageCount = pages.getTotalPages() / pageSize + 1;
//        pageOrderModel.setPageCount(pageCount);
//        pageOrderModel.setPageSize(pageSize);
//        pageOrderModel.setPageIndex(page);
//        pageOrderModel.setTotal(pages.getTotalPages());
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Sort sort = new Sort(Sort.Direction.DESC, "addTime");
        Page<UserTempIntegralHistory> pages = userTempIntegralHistoryService.getPageByUserId(userId,
                1, 500, page - 1, pageSize, sort);
        PageOrderModel pageOrderModel = new PageOrderModel();
        List<OrderHistoryModel> list = new ArrayList<>();
        pages.getContent().stream().filter(history -> history.getIntegral() > 0).forEach(history -> {
            User contributeUser = userRepository.getOne(history.getContributeUserID());
            if (null != contributeUser) {
                OrderHistoryModel model = new OrderHistoryModel();
                model.setAddDate(time.format(history.getAddTime()));
                model.setIntegral(history.getIntegral());
                model.setLoginName(contributeUser.getLoginName());
                model.setMobile(contributeUser.getMobile());
                Order order = history.getOrder();
                model.setOrderId(order.getId());
                model.setOrderName(order.getTitle());
                model.setOrderStatus(order.getStatus());
                model.setPrice(order.getPrice());
                list.add(model);
            }
        });
        pageOrderModel.setRows(list);
        int pageCount = pages.getTotalPages() / pageSize + 1;
        pageOrderModel.setPageCount(pageCount);
        pageOrderModel.setPageSize(pageSize);
        pageOrderModel.setPageIndex(page);
        pageOrderModel.setTotal(pages.getTotalPages());
        return pageOrderModel;
    }

    /**
     * 商品详情页，也是可以分享的页面
     *
     * @param merchantId
     * @param userId
     * @return
     * @throws IOException
     */
    private String getSisGoodsUrl(long merchantId, long userId) throws IOException {
        String url = getMerchantSubDomain(merchantId);
        url = url.replace("http://", "http://s" + userId + ".");
        StringBuilder sb = new StringBuilder(url);
        sb.append("/Mall/View.aspx?customerid=" + merchantId + "&gduid=" + userId + "&goodsid=");
        return sb.toString();
    }

    /**
     * 获取当前登录的user
     *
     * @return
     */
    private Long getCurrentUserId() {
        PublicParameterModel ppm = PublicParameterHolder.get();
        Long userId = ppm.getUserId();
        return userId;
    }


    /**
     * 计算直推奖
     * <p>
     * -(1)、插入流水表Hot_UserTempIntegral_History
     * -(2)、用户表Hot_UserBaseInfo-》UB_UserTempIntegral更新
     *
     * @param httpServletRequest request请求
     * @return
     * @throws Exception
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/calculateShopRebate", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResultModel calculateShopRebate(HttpServletRequest httpServletRequest) throws Exception {
        String sign = httpServletRequest.getParameter("sign");
        String shopIdString = httpServletRequest.getParameter("shopId");
        int shopId = Integer.parseInt(shopIdString);
        String orderId = httpServletRequest.getParameter("orderId");
        String unionOrderId = httpServletRequest.getParameter("unionOrderId");
        ResultModel resultModel = new ResultModel();
//        if (sign != "9389e8a5c32eefa3134340640fb4ceaa\n") {
//            resultModel.setCode(401);
//            resultModel.setMessage("签名失败");
//            return resultModel;
//
//        }
        System.out.println(shopId + "" + orderId + "" + unionOrderId);
        Order order = sisOrderRepository.findOne(orderId);
        List<OrderItems> orderItems = sisOrderItemsRepository.getOrderItemsByOrderId(orderId);

        for (OrderItems i : orderItems) {//测试得到orderItems对象
            System.out.println(i);
        }
        SisLevel sisLevel = sisLevelRepository.findOne((long) shopId);
        System.out.println("sislevel rate:" + sisLevel.getRebateRate());
//        int sisShopLevel=sisLevel.getLevelNo().intValue();
//        SisConfig sisConfig=sisConfigRepository.findByMerchantId(order.getMerchant().getId());
//        String rebate=sisConfig.getRebateSetting();
        double rate = 0;//所需要计算出来的那个比例值
        rate = sisLevel.getRebateRate();
        double totalPrize = 0;//总共的直推积分
        for (int i = 0; i < orderItems.size(); i++) {
            int zhituiPrize = (int) orderItems.get(i).getZhituiPrize();
            totalPrize += zhituiPrize;
        }
        totalPrize = (totalPrize * rate);
        UserTempIntegralHistory utih = new UserTempIntegralHistory();
        int totalPrizeInt = (int) totalPrize;
        System.out.println(totalPrizeInt);
        utih.setIntegral(totalPrizeInt);
        utih.setUnionOrderId(unionOrderId);
        utih.setStatus(0);
        utih.setOrder(order);
        utihRepository.save(utih);
        resultModel.setCode(200);
        resultModel.setMessage("签名成功");
        return resultModel;

    }

    /**
     * 根据店中店商品列表，计算直推返利/分销返利区间 后再返回
     *
     * @param sisGoodsList
     * @param user
     * @return
     */
    private List<PcSisGoodsModel> calculateValue(List<SisGoods> sisGoodsList, User user) throws IOException, SisException {
        List<PcSisGoodsModel> list = new ArrayList<>();
        //
        Sis sis = sisRepository.findByUser(user);
        if (sis == null) {
            throw new SisException("无法查询到店主信息");
        }
        SisLevel level = sis.getSisLevel();
        if (level == null) {
            throw new SisException("店铺等级未配置");
        }
        UserLevel userLevel = userLevelRepository.findOne(Long.parseLong(user.getLevelId() + ""));
        if (userLevel == null) {
            throw new SisException("会员等级未配置");
        }
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(user.getMerchant().getId());
        if(sisConfig==null||sisConfig.getEnabled()==0){
            throw new SisException("未找到店中店配置或未启用店中店");
        }
        MerchantConfig merchantConfig = merchantConfigRepository.findByMerchant(user.getMerchant());
        int exchangeRate = 100; //默认100
        RebateInfo merchantRebateInfo = new RebateInfo();
        //
        if (null != merchantConfig) {
            if (merchantConfig.getExchangeRate() != 0) {
                exchangeRate = merchantConfig.getExchangeRate();
            }

            merchantRebateInfo.setMode(merchantConfig.getRebateMode());
            if (merchantConfig.getRebateMode().equals(RebateMode.BySale)) {
                merchantRebateInfo.setRebate(merchantConfig.getBySale());
            } else {
                merchantRebateInfo.setRebate(merchantConfig.getByQuato());
            }
        }
        String resoureServerUrl = commonConfigService.getResoureServerUrl();
        StringBuffer stringBuffer = new StringBuffer();
        String webUrl = commonConfigsService.getWebUrl();
        if (Objects.isNull(webUrl)) {
            throw new SisException("店中店网站地址未配置");
        }
        stringBuffer.append(webUrl);
        stringBuffer.append("/sisweb/getSisGoodsDetail?customerId=" + user.getMerchant().getId());
        String goodsUrl = stringBuffer.toString();

        String goodsShareUrl = getSisGoodsUrl(user.getMerchant().getId(), user.getId());

        if (null != sisGoodsList && sisGoodsList.size() > 0) {
            for (SisGoods sisGoods : sisGoodsList) {
                Goods goods = sisGoods.getGoods();
                PcSisGoodsModel appSisGoodsModel = new PcSisGoodsModel();

                Integer directRebate;
                Integer pushModel=sisConfig.getPushAwardMode();
                //暂时按照直推奖的最小值来显示
                switch (pushModel){
                    case 0:
                        directRebate = (int) Math.rint(goods.getShopRebateMin() * level.getRebateRate() / exchangeRate);
                        break;
                    case 1:
                        List<ProIdAndAmount>proIdAndAmounts=goods.getShopRebates();
                        double amount=0;
                        if(proIdAndAmounts!=null&&!proIdAndAmounts.isEmpty()){
                             amount=proIdAndAmounts.stream().mapToDouble(ProIdAndAmount::getAmount).min().orElse(0);
                        }
                        SisRebateTeamManagerSetting setting=sisConfig.getSisRebateTeamManagerSetting();
                        double rate=sisService.countPushPercent(level.getLevelNo(),setting.getManageAwards());
                        rate+=setting.getSaleAward();
                        double totalAmount=amount*rate/100;
                        directRebate= MathHelper.getIntegralRateByRate(totalAmount,exchangeRate);
                        break;
                    default:
                        directRebate=0;
                }
                appSisGoodsModel.setDirectRebate(directRebate);

                List<Double> memberPrices = new ArrayList<>();
                if (goods.getPricesCache() != null && goods.getPricesCache().size() > 0) {
                    memberPrices.addAll(goods.getPricesCache().entrySet().stream().filter(entry -> entry.getValue().getLevel() == user.getLevelId()).map(entry -> entry.getValue().getMinPrice()).collect(Collectors.toList()));
                } else {
                    memberPrices.add(goods.getPrice());
                }

                if (memberPrices.size() > 0) {
                    double minPrice = memberPrices.get(0);
                    for (Double nowPrice : memberPrices) {
                        if (null != nowPrice && minPrice > nowPrice) {
                            minPrice = nowPrice;
                        }
                    }
                    if (!Objects.isNull(minPrice) && minPrice != 0) {
                        appSisGoodsModel.setPrice(minPrice);
                    } else {
                        appSisGoodsModel.setPrice(goods.getPrice());
                    }
                } else {
                    appSisGoodsModel.setPrice(goods.getPrice());
                }

                //如果是普通会员
                if (userLevel.getType().equals(UserType.normal)) {
                    appSisGoodsModel.setMinRebate(0);
                    appSisGoodsModel.setMaxRebate(0);
                } else {
                    //三级返利
                    if (null != merchantConfig && null != merchantConfig.getRebateCompatible() && merchantConfig.getRebateCompatible().equals(RebateCompatible.ThreeMode)) {
                        //将所有等级的价格都得到，然后去重
                        Set<Double> prices = new HashSet<>();
                        List<Double> allIntegral = new ArrayList<>();
                        if (goods.getPricesCache() != null && goods.getPricesCache().size() > 0) {
                            for (Map.Entry<Long, LevelPrice> entry : goods.getPricesCache().entrySet()) {
                                prices.add(entry.getValue().getMinPrice());
                                prices.add(entry.getValue().getMaxPrice());
                            }
                        } else {
                            prices.add(goods.getPrice());
                        }

                        //是否个性化
                        if (goods.getIndividuation() != null && goods.getIndividuation() == true) {
                            RebateInfo rebateInfo = new RebateInfo();
                            RebateConfiguration rebateConfiguration = goods.getRebateConfiguration();
                            if (null != rebateConfiguration) {
                                //按销售额
                                rebateInfo.setMode(rebateConfiguration.getMode());
                                if (rebateConfiguration.getMode().equals(RebateMode.BySale)) {
                                    rebateInfo.setRebate(rebateConfiguration.getSale());
                                    rebateInfo.setRatio(rebateConfiguration.getSaleRatio());
                                    if (prices.size() > 0) {
                                        for (Double price : prices) {
                                            rebateInfo.setAmount(price);
                                            //得到未处理的返利
                                            Double integral = normalRebateService.rebateAmountByLevelId(user.getLevelId(), rebateInfo, 1);
                                            allIntegral.add(integral);
                                        }
                                    } else {
                                        rebateInfo.setAmount(goods.getPrice());
                                        Double integral = normalRebateService.rebateAmountByLevelId(user.getLevelId(), rebateInfo, 1);
                                        allIntegral.add(integral);
                                    }

                                } else {
                                    //按配额
                                    rebateInfo.setRebate(rebateConfiguration.getQuato());
                                    rebateInfo.setRatio(rebateConfiguration.getQuatoRadio());
                                    if (prices.size() > 0)
                                        prices.stream().forEach(price -> {
                                            rebateInfo.setAmount(price);
                                            //得到未处理的返利
                                            Double integral = normalRebateService.rebateAmountByLevelId(user.getLevelId(), rebateInfo, 1);
                                            allIntegral.add(integral);
                                        });
                                    else {
                                        rebateInfo.setAmount(goods.getPrice());
                                        Double integral = normalRebateService.rebateAmountByLevelId(user.getLevelId(), rebateInfo, 1);
                                        allIntegral.add(integral);
                                    }
                                }
                            }
                        } else {
                            RebateConfiguration rebateConfiguration = goods.getRebateConfiguration();
                            if (null != rebateConfiguration) {
                                if (merchantConfig.getRebateMode().equals(RebateMode.BySale)) {
                                    merchantRebateInfo.setRatio(rebateConfiguration.getSaleRatio());
                                } else {
                                    merchantRebateInfo.setRatio(rebateConfiguration.getQuatoRadio());
                                }
                            }
                            if (prices.size() > 0) for (Double price : prices) {
                                merchantRebateInfo.setAmount(price);
                                //得到未处理的返利
                                Double integral = normalRebateService.rebateAmountByLevelId(user.getLevelId(), merchantRebateInfo, 1);
                                allIntegral.add(integral);
                            }
                            else {
                                merchantRebateInfo.setAmount(goods.getPrice());
                                //得到未处理的返利
                                Double integral = normalRebateService.rebateAmountByLevelId(user.getLevelId(), merchantRebateInfo, 1);
                                allIntegral.add(integral);
                            }
                        }
                        if (allIntegral.size() > 0) {
                            Double minIntegral = allIntegral.get(0);
                            Double maxIntegral = allIntegral.get(0);
                            for (Double integral : allIntegral) {
                                if (minIntegral > integral)
                                    minIntegral = integral;
                                if (maxIntegral < integral)
                                    maxIntegral = integral;
                            }
                            appSisGoodsModel.setMinRebate((int) Math.rint(minIntegral * 100 / exchangeRate));
                            appSisGoodsModel.setMaxRebate((int) Math.rint(maxIntegral * 100 / exchangeRate));
                        } else {
                            appSisGoodsModel.setMinRebate(0);
                            appSisGoodsModel.setMaxRebate(0);
                        }
                    } else if (null != merchantConfig && null != merchantConfig.getRebateCompatible() && merchantConfig.getRebateCompatible().equals(RebateCompatible.EightMode)) {
                        if (null == goods.getProductRebateConfigs()) {
                            appSisGoodsModel.setMinRebate(0);
                            appSisGoodsModel.setMaxRebate(0);
                        } else {
                            double[] integral = advanceQuatoRebateService.rebateAmountIntervalByLevelId(user.getLevelId()
                                    , goods.getProductRebateConfigs(), merchantConfig.getRebateLayerConfigs(), 1);
                            appSisGoodsModel.setMinRebate((int) Math.rint(integral[0] * 100 / exchangeRate));
                            appSisGoodsModel.setMaxRebate((int) Math.rint(integral[1] * 100 / exchangeRate));
                        }
                    }else if(null != merchantConfig && null != merchantConfig.getRebateCompatible() && merchantConfig.getRebateCompatible().equals(RebateCompatible.operator)){
                        Integer maxRebate=0;
                        Integer minRebate=0;
                        Integer levelNo=null;
                        List<UserLevel> userLevels=userLevelRepository.findByMerchant_IdAndTypeOrderByLevelAsc(user.getMerchant().getId(),
                                UserType.buddy,new PageRequest(0,1000)).getContent();
                        if(userLevels!=null){
                            for(int i=0;i<userLevels.size();i++){
                                if(userLevels.get(i).getId().equals((long)user.getLevelId())){
                                    levelNo=i;
                                    break;
                                }
                            }
                            if(levelNo!=null){
                                RebateTeamManagerSetting rebateSetting=merchantConfig.getRebateTeamManagerSetting();
                                List<String> levelKeys=getPossibleRebateRelations(-1,levelNo);
                                if(rebateSetting!=null&&levelKeys!=null){
                                    Double rebate=rebateSetting.getSaleAward();
                                    for(RebateTeam rt:rebateSetting.getRebateTeams()){
                                        for(String s:levelKeys){
                                            if(rt.getRelation().equals(s)){
                                                rebate=rebate+rt.getPercent();
                                            }
                                        }
                                    }
                                    List<ProductDisRebateDesc> productRebateConfigs=goods.getProductRebateConfigs();
                                    if(productRebateConfigs!=null){
                                        double maxAmount=getProductDisRebateDescsMaxAmount(productRebateConfigs);
                                        maxRebate=(int)Math.rint(maxAmount*rebate/exchangeRate);
                                        minRebate=maxRebate;
                                    }
                                }
                            }
                        }
                        appSisGoodsModel.setMinRebate(minRebate);
                        appSisGoodsModel.setMaxRebate(maxRebate);
                    } else {
                        appSisGoodsModel.setMinRebate(0);
                        appSisGoodsModel.setMaxRebate(0);
                    }
                }

                appSisGoodsModel.setGoodsId(goods.getId());
                appSisGoodsModel.setGoodsName(goods.getTitle());
                if (null != goods.getSmallPic().getValue() && goods.getSmallPic().getValue().length() > 0) {
                    appSisGoodsModel.setImgUrl(resoureServerUrl + goods.getSmallPic().getValue());//图片路径
                } else {
                    appSisGoodsModel.setImgUrl(img_path);
                }
//                appSisGoodsModel.setPrice(goods.getPrice());
                appSisGoodsModel.setProfit(goods.getPrice() - goods.getCost());
                appSisGoodsModel.setStock(goods.getStock());
                appSisGoodsModel.setGoodSelected(sisGoods.isSelected());
                appSisGoodsModel.setDetailsUrl(goodsUrl + "&goodId=");
                appSisGoodsModel.setShelves(sis.getShelvesAllGoods()==null? false:sis.getShelvesAllGoods());
//                logger.debug("sis goods share URL:" + getSisGoodsUrl(user.getMerchant().getId(), user.getId(), goods.getId()));
                appSisGoodsModel.setShareUrl(goodsShareUrl + goods.getId());
                list.add(appSisGoodsModel);
            }
        }
        return list;
    }

    /**
     * 获取我的下级会员开店的贡献奖，和每个级别的人数
     *
     * @param model
     * @return
     * @throws UserNotFoundException
     * @throws SisException
     * @throws IOException
     */
    @RequestMapping(value = "/ownerJuniorList", method = RequestMethod.GET)
    public String getOwnerJuniorList(Long customerId, Model model) throws UserNotFoundException, SisException, IOException {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new UserNotFoundException("用户不存在");
        }
        User user = userRepository.findOne(userId);
        if (null == user)
            throw new SisException("该用户不存在或者已经过期");

        List<SisSumAmountModel> list = sqlService.getListGroupBySrcType(userId);
        model.addAttribute("list", list);
        model.addAttribute("customerId", customerId);
        return "/sisweb/ownerJuniorList";
    }


    /**
     * 店铺订单页面
     *
     * @param srcType
     * @param customerId
     * @param model
     * @return
     * @throws IOException
     * @throws UserNotFoundException
     * @throws SisException
     */
    @RequestMapping(value = "/juniorDetailList", method = RequestMethod.GET)
    public String getJuniorDetailList(Integer srcType, Long customerId, Model model) throws IOException, UserNotFoundException, SisException {

        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new UserNotFoundException("用户不存在");
        }
        User user = userRepository.findOne(userId);
        if (null == user)
            throw new SisException("该用户不存在或者已经过期");

        model.addAttribute("customerId", customerId);
        model.addAttribute("srcType", srcType);
        return "/sisweb/juniorDetailList";
    }

    /**
     * 店铺订单list
     *
     * @param srcType    层级
     * @param customerId 商户ID
     * @param pageSize   每页条数
     * @param page       页数
     * @return
     * @throws IOException
     * @throws UserNotFoundException
     * @throws SisException
     */
    @RequestMapping(value = "/juniorDetailListAjax", method = {RequestMethod.POST})
    @ResponseBody
    public PageOpenShopModel getJuniorDetailListAjax(Integer srcType, Long customerId, Integer pageSize, Integer page) throws
            IOException, UserNotFoundException, SisException {
        System.out.println("srcType:"+srcType);
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw new UserNotFoundException("用户不存在");
        }
        User user = userRepository.findOne(userId);
        if (null == user)
            throw new SisException("该用户不存在或者已经过期");

        Page<SisDetailModel> sisDetailModel = sqlService.getListOpenShop(userId, srcType, page, pageSize);
        PageOpenShopModel pageOpenShopModel = new PageOpenShopModel();
        pageOpenShopModel.setRows(sisDetailModel.getContent());
        pageOpenShopModel.setPageCount(sisDetailModel.getTotalPages());
        pageOpenShopModel.setPageIndex(page);
        pageOpenShopModel.setTotal(Integer.parseInt(sisDetailModel.getTotalElements() + ""));
        pageOpenShopModel.setPageSize(pageSize);
        return pageOpenShopModel;
    }

    /**
     * 返回到选取商品页面
     *
     * @param model
     * @throws IOException
     * @throws SisException
     */
    @RequestMapping(value = "/recommendGoodsIndex", method = RequestMethod.GET)
    public String recommendGoodsIndex(Long customerId,Model model) throws IOException, SisException {
        Long userId = getCurrentUserId();
        User user = userRepository.findOne(userId);
        if (null == user) {
            throw new SisException("该用户不存在或者已经过期");
        }
        Sis sis =sisRepository.findByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("customerId", customerId);
        Long count = sisGoodsService.countByUserId(customerId,userId);
        model.addAttribute("count", count);
        if(sis!=null){
            model.addAttribute("shelvesModel",sis.getShelvesAllGoods()==null?false:sis.getShelvesAllGoods());
        }

        return "/sisweb/recommendGoodsIndex";
    }

    /**
     * 获取商家推荐的商品
     * @param page          第几页
     * @param pageSize      每页条数
     * @param keywords      商品民称
     * @param customerId    商户ID
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getRecommendGoodsList",method = RequestMethod.POST)
    @ResponseBody
    public PageGoodsModel getRecommendGoodsList(int page, int pageSize,Long customerId,
                                                @RequestParam(required = false) String keywords) throws Exception {
        Long userId = getCurrentUserId();
        User user=userRepository.findOne(userId);
        if(user==null){
            throw new UserNotFoundException("用户不存在或已过期");
        }
        Page<SisGoods> sisGoodsList = sisGoodsRecommendService.findSisRecommendGoodsModel
                (customerId,user,keywords,new PageRequest(page-1,pageSize));
//        if (goodsList != null && goodsList.getContent() != null) {
//            goodsList.forEach(p -> {
//                List<SisGoods> sisGoodsListTemp = sisGoodsService.getAllSisGoodsList(p.getId(), user.getId(),
//        user.getMerchant().getId());
//                if (sisGoodsListTemp != null && sisGoodsListTemp.size() == 1) {
//                    sisGoodsList.add(sisGoodsListTemp.get(0));
//                } else {
//                    SisGoods sisGoods = new SisGoods();
//                    sisGoods.setGoods(p);
//                    sisGoods.setSelected(false);    //设置成未上架
//                    sisGoodsList.add(sisGoods);
//                }
//            });
//        }
        //计算直推返利/分销返利值
        List<PcSisGoodsModel> list = calculateValue(sisGoodsList.getContent(), user);
        //
        PageGoodsModel model = new PageGoodsModel();
        int count = (int)sisGoodsList.getTotalElements();
        int pageCount = sisGoodsList.getTotalPages();
        model.setRows(list);
        model.setPageCount(pageCount);
        model.setPageSize(pageSize);
        model.setPageIndex(page);
        model.setTotal(count);

        return model;
    }


    /**
     *
     * 经营者模式，根据输入的小伙伴等级索引找出所有应该的返利的key
     * @param startIndex   开始的小伙伴等级
     * @param stopIndex    结束的小伙伴等级
     * @return
     */
    private List<String> getPossibleRebateRelations(int startIndex, int stopIndex) {
        List<String> lstResults = new ArrayList<>();
        boolean flgSameLevel = startIndex == stopIndex;
        int leftTopIndex = flgSameLevel ? stopIndex : stopIndex - 1;
        int rightTopIndex = stopIndex;
        for (int i = startIndex; i <= leftTopIndex; i++) {
            for (int k = startIndex; k <= rightTopIndex; k++) {
                int diff = k - i;
                if (diff == 1 || diff == 0) {
                    lstResults.add(i+"_"+k);
                }
            }
        }
        return lstResults;
    }

    /**
     * 获取商品八级返利冗余字段里面返利最多的货品的钱
     * @param productDisRebateDescs
     * @return
     */
    private double getProductDisRebateDescsMaxAmount(List<ProductDisRebateDesc> productDisRebateDescs){
        double maxAmount=0;
        for(ProductDisRebateDesc p:productDisRebateDescs){
            if(p.getAmount()>maxAmount){
                maxAmount=p.getAmount();
            }
        }
        return maxAmount;
    }

}
