package com.huotu.sis.repository;

import cm.huotu.sis.common.WebTest;
import com.huotu.huobanplus.common.entity.Merchant;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.repository.mall.MerchantRepository;
import com.huotu.sis.repository.mall.UserRepository;
import com.huotu.sis.service.SisLevelService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/11.
 */
public class SisRepositoryTest extends WebTest {
    @Autowired
    private SisRepository sisRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private SisLevelRepository sisLevelRepository;

    @Autowired
    private SisLevelService sisLevelService;

    @Test
    public void testFindByUserWhereUserBeloneis() throws Exception {
        Merchant merchant=new Merchant();
        merchant.setLoginName("test");
        merchant=merchantRepository.saveAndFlush(merchant);

        User four=new User();
        four.setMerchant(merchant);
        four=userRepository.saveAndFlush(four);


        User three=new User();
        three.setMerchant(merchant);
        three.setBelongOne(four.getId());
        three=userRepository.saveAndFlush(three);


        User two=new User();
        two.setMerchant(merchant);
        two.setBelongOne(four.getId());
        two=userRepository.saveAndFlush(two);

        User one=new User();
        one.setMerchant(merchant);
        one.setBelongOne(four.getId());
        one=userRepository.saveAndFlush(one);

        User own=new User();
        own.setMerchant(merchant);
        own.setBelongOne(four.getId());
        own=userRepository.saveAndFlush(own);




        SisLevel ownsl=new SisLevel();
        ownsl.setLevelNo(0);
        ownsl.setUpShopNum(0);
        ownsl.setMerchantId(merchant.getId());
        ownsl=sisLevelRepository.saveAndFlush(ownsl);

        SisLevel twosl=new SisLevel();
        twosl.setLevelNo(1);
        twosl.setUpShopNum(0);
        twosl.setMerchantId(merchant.getId());
        twosl=sisLevelRepository.saveAndFlush(twosl);

        SisLevel sisLevel=sisLevelRepository.findFirstByMerchantIdAndLevelNoGreaterThanOrderByLevelNoAsc(merchant.getId(),0);




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

        sis=new Sis();
        sis.setUser(two);
        sis.setCustomerId(merchant.getId());
        sis.setSisLevel(twosl);
        sis=sisRepository.saveAndFlush(sis);

        sis=new Sis();
        sis.setUser(three);
        sis.setCustomerId(merchant.getId());
        sis.setSisLevel(twosl);
        sis=sisRepository.saveAndFlush(sis);


        sis=new Sis();
        sis.setUser(four);
        sis.setCustomerId(merchant.getId());
        sis.setSisLevel(ownsl);
        sis=sisRepository.saveAndFlush(sis);



        List<Sis> sises=sisRepository.findByUserWhereUserBeloneis(four.getId());
        Assert.assertEquals("",sises.size(),4);


        Map<Long,Integer> map=sisLevelService.getEachSisLevelNum(four);


        Assert.assertEquals("",map.get(ownsl.getId()).intValue(),2);
        Assert.assertEquals("",map.get(0L).intValue(),4);

    }

    @Test
    public void findFirstByMerchantIdAndLevelNoGreaterThanOrderByLevelNoAscTest(){

    }
}