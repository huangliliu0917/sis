package com.huotu.sis.service.impl;

import com.huotu.huobanplus.common.entity.MallCptStockLog;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.repository.SisConfigRepository;
import com.huotu.sis.repository.mall.MallCptCfgRepository;
import com.huotu.sis.repository.mall.MallCptStockLogRepository;
import com.huotu.sis.service.MallCptStockLogService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/2/1.
 */
@Service
public class MallCptStockLogServiceImpl implements MallCptStockLogService {
    @Autowired
    MallCptStockLogRepository mallCptStockLogRepository;
    @Autowired
    MallCptCfgRepository mallCptCfgRepository;
    @Autowired
    SisConfigRepository sisConfigRepository;
    private Log log = LogFactory.getLog(MallCptStockLogServiceImpl.class);

    @Override
    public MallCptStockLog saveCptStockLogs(Long customerId, Double cumulativeAmount, Integer stockNum,
                                            User member, User contribMember, String orderId) throws Exception {
        if(stockNum<=0){
            log.info("user"+member.getId()+"stockNum is 0");
            return null;
        }
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyyMM");
        Calendar calendar=Calendar.getInstance();
        Long monthTag=Long.parseLong(sFormat.format(calendar.getTime()));
        calendar.add(Calendar.MONTH,1);
        Long settleMonthTag=Long.parseLong(sFormat.format(calendar.getTime()));

        MallCptStockLog mallCptStockLog=new MallCptStockLog();
        mallCptStockLog.setAddTime(new Date());
        mallCptStockLog.setConsumeDeduct(0.0);
        mallCptStockLog.setConsumeOddChange(0.0);
        mallCptStockLog.setMemberId(member.getId());
        mallCptStockLog.setContribMemberId(contribMember.getId());
        mallCptStockLog.setMonthTag(monthTag);
        mallCptStockLog.setSettleMonthTag(settleMonthTag);
        mallCptStockLog.setRate(cumulativeAmount);
        mallCptStockLog.setOrderId(orderId);
        mallCptStockLog.setStockNum(stockNum);//送的股数
        mallCptStockLog.setCustomerId(customerId);
        mallCptStockLog.setSrcType(300);
        mallCptStockLog.setRemark("开店获得");
        mallCptStockLog=mallCptStockLogRepository.save(mallCptStockLog);
        return mallCptStockLog;
    }
}
