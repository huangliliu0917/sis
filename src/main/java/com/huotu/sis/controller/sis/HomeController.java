package com.huotu.sis.controller.sis;

/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 进入后台首页，测试的时候使用，正式发布时通过伙伴商城进入
 * Created by slt on 2016/1/22.
 */
@Controller
@RequestMapping("/sis")
public class HomeController {
    @RequestMapping("/home")
    public String abc() {
        return "/sis/home";
    }
}
