package com.huotu.sis.controller.sisweb;

import com.huotu.sis.exception.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinzj on 2016/1/12.
 */
@Controller
@ControllerAdvice
public class ControllerEnhance {

    private Log log = LogFactory.getLog(ControllerEnhance.class);

    @ExceptionHandler({CustomerNotFoundException.class,SisException.class, CustomerNotUseSisException.class,
            UserNotFoundException.class, UserNotOpenShopQualificationException.class,
            GoodsNotFoundException.class,ProductNotFoundException.class})
    public ModelAndView sawVoteFailedException(Exception ex, HttpServletRequest request) throws IOException {
        if (request.getParameter("isAjax") == null) {
            return errorPage(ex.getLocalizedMessage());
        } else {
            ModelAndView modelAndView = new ModelAndView();
            MappingJackson2JsonView view = new MappingJackson2JsonView();
            Map map = new HashMap<>();
            map.put("resultCode", 0);
            map.put("resultMsg", ex.getMessage());
            view.setAttributesMap(map);
            modelAndView.setView(view);
            return modelAndView;
        }
    }


    private ModelAndView errorPage(String message) throws IOException {
        ModelAndView modelAndView = new ModelAndView("/html/error");
        modelAndView.addObject("errorMessage", message);
        return modelAndView;
    }


}
