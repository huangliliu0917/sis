package com.huotu.sis.controller.sis;

/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

import com.huotu.huobanplus.sdk.mall.annotation.CustomerId;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.repository.SisLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

/**
 * Created by jinzj on 2016/1/21.
 *
 * @author jinzj
 * @
 */
@Controller
@RequestMapping("/sisLevel")
public class SisLevelController {

    @Autowired
    private SisLevelRepository sisLevelRepository;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(@CustomerId Long customerId, Model model) throws IOException {
        if (null == customerId)
            throw new IOException("");
        List<SisLevel> list = sisLevelRepository.findByMerchantId(customerId);
        model.addAttribute("levelList", list);
        return null;
    }

    @RequestMapping(value = "/saveLevel", method = RequestMethod.GET)
    public ModelMap saveLevel(Long customerId, String levelName,
                              @RequestParam(required = false)String remark,
                              @RequestParam(required = false)Integer upShopNum) {

        return null;
    }
}
