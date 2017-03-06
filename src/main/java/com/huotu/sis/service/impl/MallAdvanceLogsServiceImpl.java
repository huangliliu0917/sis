package com.huotu.sis.service.impl;

import com.huotu.huobanplus.common.entity.MallAdvanceLogs;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.repository.SisRepository;
import com.huotu.sis.repository.mall.MallAdvanceLogsRepository;
import com.huotu.sis.repository.mall.OrderRepository;
import com.huotu.sis.repository.mall.UserRepository;
import com.huotu.sis.service.MallAdvanceLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Administrator on 2016/1/29.
 */
@Service
public class MallAdvanceLogsServiceImpl implements MallAdvanceLogsService {
    @Autowired
    MallAdvanceLogsRepository mallAdvanceLogsRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SisRepository sisRepository;
    @Override
    public MallAdvanceLogs saveMallAdvanceLogs(User earningsUser,User contributeUser, Double money, String orderId,
                                               String unionOrderId,Integer srcType) throws Exception {


        MallAdvanceLogs mallAdvanceLogs=new MallAdvanceLogs();
        if(money==0){
            return mallAdvanceLogs;
        }
        mallAdvanceLogs.setMemberId(earningsUser.getId());
        mallAdvanceLogs.setMoney(money);
        mallAdvanceLogs.setMessage("");
        mallAdvanceLogs.setMTime(new Date());
        mallAdvanceLogs.setPaymentId("");
        mallAdvanceLogs.setOrderId(orderId);
        mallAdvanceLogs.setPayMethod("开店奖");
        String memo="开店获得";
        if(srcType!=0){
            memo=srcType+"级会员"+contributeUser.getLoginName()+"("+contributeUser.getWxNickName()+")贡献了开店奖";
        }
        mallAdvanceLogs.setMemo(memo);//例:三级会员登录名(昵称)贡献了开店奖
        mallAdvanceLogs.setImportMoney(money);
        mallAdvanceLogs.setShopAdvance(0.0);
        mallAdvanceLogs.setExplodeMoney(0.0);
        mallAdvanceLogs.setMemberAdvance(0.0);
        mallAdvanceLogs.setDisabled(0);
        mallAdvanceLogs.setCustomerId(earningsUser.getMerchant().getId());
        mallAdvanceLogs.setUnionOrderId(unionOrderId);
        mallAdvanceLogs=mallAdvanceLogsRepository.save(mallAdvanceLogs);
        return mallAdvanceLogs;
    }
}
