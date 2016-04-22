package com.huotu.sis.controller;

import com.huotu.huobanplus.common.entity.Brand;
import com.huotu.huobanplus.common.entity.Category;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.repository.BrandRepository;
import com.huotu.huobanplus.common.repository.CategoryRepository;
import com.huotu.huobanplus.common.repository.UserRepository;
import com.huotu.sis.common.PublicParameterHolder;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisBrand;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.exception.SisException;
import com.huotu.sis.model.AppSisSortModel;
import com.huotu.sis.model.PageSisBrandModel;
import com.huotu.sis.model.PublicParameterModel;
import com.huotu.sis.model.SisBrandModel;
import com.huotu.sis.repository.SisConfigRepository;
import com.huotu.sis.repository.SisRepository;
import com.huotu.sis.service.CommonConfigService;
import com.huotu.sis.service.CommonConfigsService;
import com.huotu.sis.service.SisBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by jinzj on 2016/3/8.
 */
@Controller
@RequestMapping("/sisweb")
public class SisWebBrandController {

    private static String img_path = "images/g1.png";

    @Autowired
    UserRepository userRepository;

    @Autowired
    private SisRepository sisRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    private SisBrandService sisBrandService;

    @Autowired
    CommonConfigService commonConfigService;

    @Autowired
    private CommonConfigsService commonConfigsService;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private SisConfigRepository sisConfigRepository;

    /**
     * 获取所属商家的商品分类
     *
     * @throws IOException
     * @throws SisException
     */
    @RequestMapping(value = "/getCategoryList", method = RequestMethod.GET)
    @ResponseBody
    public List<AppSisSortModel> getCategoryList() throws IOException, SisException {
        Long userId = getCurrentUserId();
        User user = userRepository.findOne(userId);
        if (null == user)
            throw new SisException("用户不存在或者已过期");

        List<Category> categories = categoryRepository.findByOwner(user.getMerchant());
        List<AppSisSortModel> sortFirst = new ArrayList<>();

        for (Category category : categories) {
            AppSisSortModel appSisSortModel = new AppSisSortModel();
            appSisSortModel.setSisId(category.getId());
            appSisSortModel.setSisName(category.getTitle());
            sortFirst.add(appSisSortModel);
        }
        return sortFirst;
    }

    /**
     * 某店主选择的品牌列表
     *
     * @param page
     * @param pageSize
     * @return
     * @throws SisException
     */
    @RequestMapping(value = "/getSisBrandList", method = RequestMethod.POST)
    @ResponseBody
    public PageSisBrandModel getBrandList(Long customerId,int page, int pageSize) throws SisException {
        Long userId = getCurrentUserId();
        User user = userRepository.findOne(userId);
        Sis sis=sisRepository.findByUser(user);
        if (null == user)
            throw new SisException("用户不存在或者已过期");
//        Page<Brand> pages = brandRepository.findByCustomerId(user.getMerchant().getId(), pageable);


        Page<SisBrand> pages=null;
        if(sis!=null&&sis.getShelvesAllGoods()!=null&&sis.getShelvesAllGoods()){
            Sort sort = new Sort(Sort.Direction.DESC, "id");
            pages = sisBrandService.findAllByUser(user, page - 1, pageSize, sort);
        }else {
            Sort sort = new Sort(Sort.Direction.DESC, "orderWeight", "id");
            pages = sisBrandService.findByUserId(userId, page - 1, pageSize, sort);

        }
        String detailsUrl ="getBrandDetail?customerId="+customerId+"&brandId=";
        List<SisBrandModel> list = new ArrayList<>();
        if (null != pages.getContent() && pages.getContent().size() > 0) {
            for (SisBrand sisBrand : pages.getContent()) {
                Brand brand = sisBrand.getBrand();
                SisBrandModel sisBrandModel = new SisBrandModel();
                sisBrandModel.setBrandId(brand.getId());
                sisBrandModel.setShelves(sis.getShelvesAllGoods()==null?false:sis.getShelvesAllGoods());
                sisBrandModel.setBrandName(brand.getBrandName());
                sisBrandModel.setCustomerId(brand.getCustomerId());
                sisBrandModel.setDetailsUrl(detailsUrl);
                if (null != brand.getBrandLogo() && brand.getBrandLogo().length() > 0) {
                    sisBrandModel.setBrandLogo(commonConfigService.getResoureServerUrl() + brand.getBrandLogo());//todo 图片路径
                } else {
                    sisBrandModel.setBrandLogo(img_path);   //todo
                }
                list.add(sisBrandModel);
            }
        }
        PageSisBrandModel model = new PageSisBrandModel();
        model.setRows(list);
        Long count = pages.getTotalElements();
        int pageCount = Integer.parseInt(count.toString()) / pageSize + 1;
        model.setPageCount(pageCount);
        model.setPageSize(pageSize);
        model.setPageIndex(page);
        model.setTotal(Integer.parseInt(count.toString()));
        return model;
    }

    /**
     * 得到商家所有的品牌（而不仅仅是店主的品牌）
     *
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/getBrandList", method = RequestMethod.POST)
    @ResponseBody
    public PageSisBrandModel getAllBrandList(Long customerId,int page, int pageSize,
                                             @RequestParam(required = false) String keywords) throws SisException {
        Long userId = getCurrentUserId();
        User user = userRepository.findOne(userId);
        if (null == user) {
            throw new SisException("用户不存在或者已过期");
        }
        Sis sis=sisRepository.findByUser(user);

        Page<Brand> brandList = sisBrandService.getAllBrandByCustomerIdAndBrandName(user.getMerchant().getId(), keywords, page, pageSize);

        List<SisBrandModel> list = new ArrayList<>();
        int count = 0;
        String detailsUrl = commonConfigsService.getWebUrl() + "/sisweb/getBrandDetail?customerId="+customerId+"&brandId=";
        if (brandList != null && brandList.getContent() != null) {
            brandList.forEach(p -> {
                SisBrandModel sisBrandModel = new SisBrandModel();
                sisBrandModel.setBrandId(p.getId());
                sisBrandModel.setBrandName(p.getBrandName());
                if(sis!=null){
                    sisBrandModel.setShelves(sis.getShelvesAllGoods()==null?false:sis.getShelvesAllGoods());
                }
                sisBrandModel.setCustomerId(p.getCustomerId());
                if (null != p.getBrandLogo() && p.getBrandLogo().length() > 0) {
                    sisBrandModel.setBrandLogo(commonConfigService.getResoureServerUrl() + p.getBrandLogo());
                } else {
                    sisBrandModel.setBrandLogo(img_path);
                }
                sisBrandModel.setDetailsUrl(detailsUrl);
                //
                List<SisBrand> sisBrandListTemp = sisBrandService.getAllSisBrandList(userId, p.getId());
                if (sisBrandListTemp != null && sisBrandListTemp.size() == 1) {
                    sisBrandModel.setSelected(sisBrandListTemp.get(0).isSelected());
                } else {
                    sisBrandModel.setSelected(false);    //设置成未上架
                }

                list.add(sisBrandModel);
            });
            count = (int) brandList.getTotalElements();
        }

        PageSisBrandModel model = new PageSisBrandModel();
        int pageCount = count / pageSize + 1;
        model.setRows(list);
        model.setPageCount(pageCount);
        model.setPageSize(pageSize);
        model.setPageIndex(page);
        model.setTotal(count);

        return model;
    }

    /**
     * “添加商品”中品牌页面
     *
     * @param model
     * @throws IOException
     * @throws SisException
     */
    @RequestMapping(value = "/getBrandListPage", method = RequestMethod.GET)
    public String getBrandListPage(Long customerId, Model model) throws IOException, SisException {
        Long userId = getCurrentUserId();
        User user = userRepository.findOne(userId);
        if (null == user)
            throw new SisException("该用户不存在或者已经过期");
        Long count = sisBrandService.countByCustomerId(userId);
        Sis sis =sisRepository.findByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("customerId", customerId);
        model.addAttribute("count", count);
        if(sis!=null){
            model.addAttribute("shelvesModel",sis.getShelvesAllGoods()==null?false:sis.getShelvesAllGoods());
        }
        return "/sisweb/sisBrandList";
    }

    /**
     * 只查找品牌id对应的品牌信息
     *
     * @param brandId
     * @param request
     * @return
     * @throws IOException
     * @throws SisException
     */
    @RequestMapping(value = "/getBrandDetail", method = RequestMethod.GET)
    public String getBrandDetail(Long customerId,Long brandId, HttpServletRequest request) throws IOException, SisException {
        Long userId = getCurrentUserId();
        User user = userRepository.findOne(userId);
        if (null == user) {
            throw new SisException("该用户不存在或者已经过期");
        }
        Brand brand = null;
        if (null != brandId) {
            brand = brandRepository.findOne(brandId);
        }
        if (null == brand) {
            throw new SisException("无法找到该品牌！");
        }
        if (null != brand.getBrandLogo() && brand.getBrandLogo().length() > 0) {
            brand.setBrandLogo(commonConfigService.getResoureServerUrl() + brand.getBrandLogo());//todo 图片路径
        } else {
            brand.setBrandLogo(img_path);   //todo
        }
        request.setAttribute("brand", brand);
        request.setAttribute("customerId", customerId);
        return "sisweb/brandDetail";
    }

    /**
     * @param brandId  品牌id
     * @param operType 上架：1
     *                 下架：0
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/operBrand", method = RequestMethod.POST)
    @ResponseBody
    public ModelMap operBrand(Long brandId, Integer operType) throws Exception {
        Long userId = getCurrentUserId();
        ModelMap modelMap = new ModelMap();
        User user = userRepository.findOne(userId);
        if (user == null) {
            modelMap.addAttribute("success", Boolean.FALSE);
            modelMap.addAttribute("msg", "用户不存在或已过期");
            return modelMap;
        }
        Long customerId = user.getMerchant().getId();
        if (operType == 2) {    //置顶
            SisBrand sisBrand = sisBrandService.getOne(brandId, userId);
            if (sisBrand == null) {
                modelMap.addAttribute("msg", "品牌已下架或已被删除");
                modelMap.addAttribute("success", Boolean.FALSE);
                return modelMap;
            }
            sisBrand.setOrderWeight(sisBrandService.getMaxWeight(userId) + 1);
            sisBrandService.save(sisBrand);
            modelMap.addAttribute("success", Boolean.TRUE);
            return modelMap;
        } else if (operType == 1) {    //上架
            Brand brand = brandRepository.findOne(brandId);
            SisBrand sisBrand = sisBrandService.getOne(brandId, userId);
            if (brand == null) {
                modelMap.addAttribute("success", Boolean.FALSE);
                modelMap.addAttribute("msg", "品牌已下架或已被删除");
                return modelMap;
            }
            if(!Objects.isNull(sisBrand)){
                modelMap.addAttribute("success", Boolean.FALSE);
                modelMap.addAttribute("msg", "品牌已经上架过了");
                return modelMap;
            }
            Long count = sisBrandService.countByCustomerId(userId);
            SisConfig sisConfig = sisConfigRepository.findByMerchantId(customerId);
            if (null != sisConfig && null != sisConfig.getMaxBrandNum()
                    && (Integer.parseInt(count.toString()) < sisConfig.getMaxBrandNum()||sisConfig.getMaxBrandNum()==0)) {
                sisBrand = new SisBrand();
                sisBrand.setSelected(Boolean.TRUE);
                sisBrand.setBrand(brand);
                sisBrand.setUser(user);
                sisBrand.setOrderWeight(sisBrandService.getMaxWeight(userId) + 1);
                sisBrandService.save(sisBrand);
                modelMap.addAttribute("success", Boolean.TRUE);
                return modelMap;
            } else {
                modelMap.addAttribute("msg", "您上架的品牌数量已达上限,请删除已上架品牌后再添加");
                modelMap.addAttribute("success", Boolean.FALSE);
                return modelMap;
            }
        } else if (operType == 0) { //下架
//            SisBrand sisBrand = sisBrandService.getOne(brandId, userId);
            List<SisBrand> sisBrandList = sisBrandService.getAllSisBrandList(userId,brandId);
            if (sisBrandList == null || sisBrandList.size()==0) {
                modelMap.addAttribute("msg", "品牌已下架或已被删除");
                modelMap.addAttribute("success", Boolean.FALSE);
                return modelMap;
            }
            sisBrandList.stream().forEach(sisBrand -> sisBrandService.delete(sisBrand));
            modelMap.addAttribute("success", Boolean.TRUE);
            return modelMap;
        }
        modelMap.addAttribute("success", Boolean.FALSE);
        modelMap.addAttribute("msg", "操作失败");
        return modelMap;
    }

    @RequestMapping(value = "/sisIndexBrand", method = RequestMethod.GET)
    public String sisIndexBrand(Long customerId, Model model) throws IOException, SisException {
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
        return "/sisweb/sisIndexBrand";
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
}
