package com.huotu.sis.controller.sis;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
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
