package com.huotu.sis.service.impl;

import cm.huotu.sis.common.WebTest;
import com.huotu.huobanplus.common.entity.Merchant;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.repository.MerchantRepository;
import com.huotu.huobanplus.common.repository.OrderRepository;
import com.huotu.huobanplus.common.repository.UserRepository;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.repository.SisConfigRepository;
import com.huotu.sis.repository.SisLevelRepository;
import com.huotu.sis.repository.SisRepository;
import com.huotu.sis.service.SisLevelService;
import com.huotu.sis.service.SisService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 2016/6/17.
 */
public class SisLevelServiceImplTest  extends WebTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    MerchantRepository merchantRepository;

    @Autowired
    SisService sisService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    SisLevelRepository sisLevelRepository;

    @Autowired
    SisConfigRepository sisConfigRepository;

    @Autowired
    SisRepository sisRepository;

    @Autowired
    SisLevelService sisLevelService;


    @Test
    public void testUpgradeSisLevelByUpShopNum() throws Exception {
        Merchant merchant=new Merchant();
        merchant.setLoginName("test");
        merchant=merchantRepository.saveAndFlush(merchant);

        User four=new User();
        four.setMerchant(merchant);
        four=userRepository.saveAndFlush(four);


//        User three=new User();
//        three.setMerchant(merchant);
//        three.setBelongOne(four.getId());
//        three=userRepository.saveAndFlush(three);


//        User two=new User();
//        two.setMerchant(merchant);
//        two.setBelongOne(three.getId());
//        two.setBelongTwo(four.getId());
//        two=userRepository.saveAndFlush(two);

        User one=new User();
        one.setMerchant(merchant);
        one.setBelongOne(four.getId());
        one=userRepository.saveAndFlush(one);

        User own=new User();
        own.setMerchant(merchant);
        own.setBelongOne(four.getId());
        own=userRepository.saveAndFlush(own);




        SisLevel threesl=new SisLevel();
        threesl.setLevelNo(3);
        threesl.setMerchantId(merchant.getId());
        threesl.setUpShopNum(3);
        threesl=sisLevelRepository.saveAndFlush(threesl);

        SisLevel twosl=new SisLevel();
        twosl.setLevelNo(2);
        twosl.setUpShopNum(2);
        twosl.setMerchantId(merchant.getId());
        twosl=sisLevelRepository.saveAndFlush(twosl);

        SisLevel onesl=new SisLevel();
        onesl.setLevelNo(1);
        onesl.setUpShopNum(1);
        onesl.setMerchantId(merchant.getId());
        onesl=sisLevelRepository.saveAndFlush(onesl);

        SisLevel ownsl=new SisLevel();
        ownsl.setLevelNo(0);
        ownsl.setUpShopNum(0);
        ownsl.setMerchantId(merchant.getId());
        ownsl=sisLevelRepository.saveAndFlush(ownsl);




        Sis sis=new Sis();
        sis.setUser(own);
        sis.setCustomerId(merchant.getId());
        sis.setSisLevel(ownsl);
        sis=sisRepository.saveAndFlush(sis);

        sis=new Sis();
        sis.setUser(one);
        sis.setCustomerId(merchant.getId());
        sis.setSisLevel(ownsl);
        sis=sisRepository.saveAndFlush(sis);

//        sis=new Sis();
//        sis.setUser(two);
//        sis.setSisLevel(onesl);
//        sis=sisRepository.saveAndFlush(sis);
//
//        sis=new Sis();
//        sis.setUser(three);
//        sis.setSisLevel(onesl);
//        sis=sisRepository.saveAndFlush(sis);

        sis=new Sis();
        sis.setUser(four);
        sis.setCustomerId(merchant.getId());
        sis.setSisLevel(ownsl);
        sis=sisRepository.saveAndFlush(sis);

        sisLevelService.upgradeSisLevelByUpShopNum(one);

        Sis fourSisnew=sisRepository.findByUser(four);
        Assert.assertEquals("",twosl.getLevelNo(),fourSisnew.getSisLevel().getLevelNo());


    }

    @Test
    public void testGetSisLevelByOfflineSisNum() throws Exception{
        User user=userRepository.findOne(120034L);

        SisLevel sisLevel=sisLevelService.getSisLevelByOfflineSisNum(user);

    }
}