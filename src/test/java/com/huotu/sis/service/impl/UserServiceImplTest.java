package com.huotu.sis.service.impl;

import cm.huotu.sis.common.WebTest;
import com.huotu.huobanplus.common.entity.Merchant;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.entity.support.LevelIdAndVal;
import com.huotu.sis.entity.support.OpenSisAward;
import com.huotu.sis.entity.support.SisLevelAward;
import com.huotu.sis.model.sisweb.SisRebateModel;
import com.huotu.sis.repository.SisConfigRepository;
import com.huotu.sis.repository.SisLevelRepository;
import com.huotu.sis.repository.SisRepository;
import com.huotu.sis.repository.mall.MerchantRepository;
import com.huotu.sis.repository.mall.OrderRepository;
import com.huotu.sis.repository.mall.UserRepository;
import com.huotu.sis.service.SisLevelService;
import com.huotu.sis.service.SisService;
import com.huotu.sis.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/7/25.
 */
public class UserServiceImplTest extends WebTest {

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

    @Autowired
    UserService userService;

    @Test
    public void testGetParentByUser() throws Exception {
        Merchant merchant=new Merchant();
        merchant.setLoginName("test");
        merchant=merchantRepository.saveAndFlush(merchant);

        User beloneOne=new User();
        User beloneTwo=new User();
        User beloneThree=new User();
        List<User> users=new ArrayList<>();

        for(int i=0;i<8;i++){
            User user=new User();
            user.setMerchant(merchant);
            user.setBelongOne(beloneOne.getId());
            user.setBelongTwo(beloneTwo.getId());
            user.setBelongThree(beloneThree.getId());
            users.add(userRepository.saveAndFlush(user));
            if(i>=0){
                beloneOne=users.get(i);
            }
            if(i-1>=0){
                beloneTwo=users.get(i-1);
            }
            if(i-2>=0){
                beloneThree=users.get(i-2);
            }
        }


        for(int i=0;i<8;i++){
            List<User> usersResult=userService.getParentByUser(users.get(users.size()-1),i);
            List<User> usersReal=users.subList(users.size()-1-i,users.size());
            for(int j=0;j<usersResult.size();j++){
                boolean flag=usersReal.get(usersReal.size()-1-i).equals(usersResult.get(i));
                Assert.assertEquals("",true,flag);
            }
        }

        SisLevel sisLevel=new SisLevel();
        sisLevel.setLevelNo(1);
        sisLevel=sisLevelRepository.saveAndFlush(sisLevel);

        List<Sis>sises=new ArrayList<>();
        for(int i=users.size()-1;i>0;i--){
            Sis sis=new Sis();
            if(i!=6){
                sis.setUser(users.get(i));
            }
            if(i!=5){
                sis.setSisLevel(sisLevel);
            }
            sis.setCustomerId(merchant.getId());
            sis.setStatus(true);
            sises.add(sisRepository.saveAndFlush(sis));
        }

        SisLevelAward sisLevelAward=new SisLevelAward();
        sisLevelAward.setBuySisLvId(0);
        List<OpenSisAward> cfg=new ArrayList<>();
        OpenSisAward openSisAward=new OpenSisAward();
        openSisAward.setUnified(5);
        openSisAward.setIdx(0);
        cfg.add(openSisAward);
        openSisAward=new OpenSisAward();
        openSisAward.setUnified(6);
        openSisAward.setIdx(1);
        cfg.add(openSisAward);
        openSisAward=new OpenSisAward();
        openSisAward.setUnified(7);
        openSisAward.setIdx(2);
        cfg.add(openSisAward);
        openSisAward=new OpenSisAward();
        openSisAward.setUnified(8);
        openSisAward.setIdx(3);
        cfg.add(openSisAward);
        openSisAward=new OpenSisAward();
        openSisAward.setUnified(-1);
        openSisAward.setIdx(4);
        List<LevelIdAndVal> levelIdAndVals=new ArrayList<>();
        LevelIdAndVal levelIdAndVal=new LevelIdAndVal();
        levelIdAndVal.setLvid(sisLevel.getId());
        levelIdAndVal.setVal(10);
        levelIdAndVals.add(levelIdAndVal);
        openSisAward.setCustom(levelIdAndVals);
        cfg.add(openSisAward);

        sisLevelAward.setCfg(cfg);

        List<SisRebateModel> sisRebateModels=userService.getSisRebateModelList(users.get(users.size()-1),sisLevelAward);

        Assert.assertEquals("",5,sisRebateModels.size());
        for(int i=0;i<sisRebateModels.size();i++){
            SisRebateModel sisRebateModel=sisRebateModels.get(i);
            if(i==0){
                Assert.assertEquals("",sisRebateModel.getUser(),users.get(users.size()-1));
                Assert.assertEquals("",new Double(5),new Double(sisRebateModel.getRebate()));
            }
            if(i==4){
                Assert.assertEquals("",sisRebateModel.getUser(),users.get(users.size()-5));
                Assert.assertEquals("",new Double(10),new Double(sisRebateModel.getRebate()));
            }

        }



    }

    @Test
    public void testGetSisRebateModel() throws Exception {

    }
}