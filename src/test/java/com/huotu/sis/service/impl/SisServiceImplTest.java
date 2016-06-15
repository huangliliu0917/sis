package com.huotu.sis.service.impl;

import cm.huotu.sis.common.WebTest;
import com.huotu.huobanplus.common.entity.Merchant;
import com.huotu.huobanplus.common.entity.Order;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.repository.MerchantRepository;
import com.huotu.huobanplus.common.repository.OrderRepository;
import com.huotu.huobanplus.common.repository.UserRepository;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.entity.support.RelationAndPercent;
import com.huotu.sis.entity.support.SisRebateTeamManagerSetting;
import com.huotu.sis.repository.SisConfigRepository;
import com.huotu.sis.repository.SisLevelRepository;
import com.huotu.sis.repository.SisRepository;
import com.huotu.sis.service.SisLevelService;
import com.huotu.sis.service.SisService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/14.
 */
public class SisServiceImplTest extends WebTest {

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
    public void testCountProprietor() throws Exception {
        Merchant merchant=new Merchant();
        merchant.setLoginName("test");
        merchant=merchantRepository.saveAndFlush(merchant);

        //订单购买者
        User orderUser=new User();
        orderUser.setMerchant(merchant);
        userRepository.saveAndFlush(orderUser);


        User four=new User();
        four.setMerchant(merchant);
        four=userRepository.saveAndFlush(four);


        User three=new User();
        three.setMerchant(merchant);
        three.setBelongOne(four.getId());
        three=userRepository.saveAndFlush(three);


        User two=new User();
        two.setMerchant(merchant);
        two.setBelongOne(three.getId());
        two.setBelongTwo(four.getId());
        two=userRepository.saveAndFlush(two);

        User one=new User();
        one.setMerchant(merchant);
        one.setBelongOne(two.getId());
        one.setBelongTwo(three.getId());
        one.setBelongThree(four.getId());
        one=userRepository.saveAndFlush(one);

        User own=new User();
        own.setMerchant(merchant);
        own.setBelongOne(one.getId());
        own.setBelongTwo(two.getId());
        own.setBelongThree(three.getId());
        own=userRepository.saveAndFlush(own);





        Order order=new Order();
        order.setId("123456789");
        order.setIsProtect(0);
        order.setIsTax(0);
        order.setUserId(orderUser.getId().intValue());
        order=orderRepository.saveAndFlush(order);


        SisLevel foursl=new SisLevel();
        foursl.setLevelNo(4);
        foursl=sisLevelRepository.saveAndFlush(foursl);

        SisLevel threesl=new SisLevel();
        threesl.setLevelNo(3);
        threesl=sisLevelRepository.saveAndFlush(threesl);

        SisLevel twosl=new SisLevel();
        twosl.setLevelNo(2);
        twosl=sisLevelRepository.saveAndFlush(twosl);

        SisLevel onesl=new SisLevel();
        onesl.setLevelNo(1);
        onesl=sisLevelRepository.saveAndFlush(onesl);

        SisLevel ownsl=new SisLevel();
        ownsl.setLevelNo(0);
        ownsl=sisLevelRepository.saveAndFlush(ownsl);


        Sis sis=new Sis();
        sis.setUser(own);
        sis.setSisLevel(twosl);
        sis=sisRepository.saveAndFlush(sis);

         sis=new Sis();
        sis.setUser(one);
        sis.setSisLevel(ownsl);
        sis=sisRepository.saveAndFlush(sis);

         sis=new Sis();
        sis.setUser(two);
        sis.setSisLevel(onesl);
        sis=sisRepository.saveAndFlush(sis);

         sis=new Sis();
        sis.setUser(three);
        sis.setSisLevel(onesl);
        sis=sisRepository.saveAndFlush(sis);

         sis=new Sis();
        sis.setUser(four);
        sis.setSisLevel(twosl);
        sis=sisRepository.saveAndFlush(sis);


        SisConfig sisConfig=new SisConfig();
        sisConfig.setMerchantId(merchant.getId());

        SisRebateTeamManagerSetting setting=new SisRebateTeamManagerSetting();
        setting.setSaleAward(60);

        List<RelationAndPercent> percents=new ArrayList<>();
        RelationAndPercent relationAndPercent=new RelationAndPercent();
        relationAndPercent.setPercent(10);
        relationAndPercent.setRelation("0_0");
        percents.add(relationAndPercent);

         relationAndPercent=new RelationAndPercent();
        relationAndPercent.setPercent(9);
        relationAndPercent.setRelation("0_1");
        percents.add(relationAndPercent);

         relationAndPercent=new RelationAndPercent();
        relationAndPercent.setPercent(8);
        relationAndPercent.setRelation("1_1");
        percents.add(relationAndPercent);

         relationAndPercent=new RelationAndPercent();
        relationAndPercent.setPercent(7);
        relationAndPercent.setRelation("1_2");
        percents.add(relationAndPercent);

         relationAndPercent=new RelationAndPercent();
        relationAndPercent.setPercent(6);
        relationAndPercent.setRelation("2_2");
        percents.add(relationAndPercent);
        setting.setManageAwards(percents);
        sisConfig.setSisRebateTeamManagerSetting(setting);
        sisConfig=sisConfigRepository.saveAndFlush(sisConfig);




        sisService.countProprietor(own,order,ownsl,sisConfig);

    }

    @Test
    public void testGetSisLevelByOfflineSisNum() throws Exception{
        User user=userRepository.findOne(1L);
        sisLevelService.getSisLevelByOfflineSisNum(user);
    }
}