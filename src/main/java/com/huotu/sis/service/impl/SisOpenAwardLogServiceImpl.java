package com.huotu.sis.service.impl;

import com.huotu.sis.common.StringHelper;
import com.huotu.sis.entity.SisOpenAwardLog;
import com.huotu.sis.repository.SisOpenAwardLogRepository;
import com.huotu.sis.service.SisOpenAwardLogService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by slt on 2016/2/19.
 */
@Service
public class SisOpenAwardLogServiceImpl implements SisOpenAwardLogService {
    private static Log log = LogFactory.getLog(SisOpenAwardLogServiceImpl.class);
    @Autowired
    SisOpenAwardLogRepository sisOpenAwardLogRepository;

    @Override
    public SisOpenAwardLog saveSisOpenAwardLog(Long customerId, Long shopId, Long contribShopId, Double amount,
                                               String remark, Integer srcType, String orderId) throws Exception {
        if(amount==null||amount<=0){
            log.info("user"+shopId+"rebate is 0");
            return null;
        }
        SisOpenAwardLog sisOpenAwardLog=new SisOpenAwardLog();
        sisOpenAwardLog.setCustomerId(customerId);
        sisOpenAwardLog.setShopId(shopId);
        sisOpenAwardLog.setContribShopId(contribShopId);
        sisOpenAwardLog.setAmount(amount);
        sisOpenAwardLog.setRemark(remark);
        Date date=new Date();
        sisOpenAwardLog.setAddTime(date);
        sisOpenAwardLog.setPosMonthTag(StringHelper.getPosMonthTag(date));
        sisOpenAwardLog.setSrcType(srcType);
        sisOpenAwardLog.setOrderId(orderId);
        sisOpenAwardLog=sisOpenAwardLogRepository.save(sisOpenAwardLog);
        return sisOpenAwardLog;
    }


}
