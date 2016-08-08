package com.huotu.sis.controller.sis;

import com.huotu.huobanplus.sdk.mall.annotation.CustomerId;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.exception.SisException;
import com.huotu.sis.repository.SisLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 店中店直推奖配置
 * Created by slt on 2016/8/4.
 */
@Controller
@RequestMapping("/sis")
public class SisPushController {

    @Autowired
    private SisLevelRepository sisLevelRepository;

    @RequestMapping("/defaultPushConfig")
    public String defaultPushConfig(@CustomerId Long customerId,Model model) throws Exception{
        if (customerId == null) {
            throw new SisException("商户ID不存在");
        }
        List<SisLevel> sisLevels=sisLevelRepository.findByMerchantIdOrderByLevelNoAsc(customerId);
        model.addAttribute("sisLevels",sisLevels);
        return "sis/defaultPushConfig";
    }
}
