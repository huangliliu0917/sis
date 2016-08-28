package com.huotu.sis.controller.sisweb;

import cm.huotu.sis.common.WebTest;
import com.huotu.huobanplus.common.entity.Merchant;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.repository.SisConfigRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2016/8/26.
 */
@Transactional(value = "transactionManager")
public class SisWebApiControllerTest extends WebTest {
    @Autowired
    SisConfigRepository sisConfigRepository;

    @Test
    public void testCalculateShopRebate() throws Exception {

        //单一商品转换为等级商品兼容测试
        SisConfig sisConfig=sisConfigRepository

    }

    @Test
    public void testOpen() throws Exception {

    }

    //创建一个店中店配置数据
    private SisConfig createSisConfig(){
        Merchant merchant=new Merchant();
        SisConfig sisConfig=new SisConfig();
        sisConfig.setMerchantId();
    }
}