package com.huotu.sis.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by lgh on 2015/12/29.
 */
@Controller
@RequestMapping("/html")
public class SisExceptionController {

    private static Log log = LogFactory.getLog(SisExceptionController.class);


    @RequestMapping("/error")
    public String returnErrorPage(String errorMessage, Model model) throws Exception{
        model.addAttribute("errorMessage",errorMessage);
        return "html/error";

    }

}
