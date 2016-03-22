package com.huotu.sis.service.impl;

import com.huotu.sis.entity.SisInviteLog;
import com.huotu.sis.repository.SisInviteRepository;
import com.huotu.sis.service.SisInviteLogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by Administrator on 2016/2/3.
 */
public class SisInviteLogServiceImpl implements SisInviteLogService {
    @Autowired
    SisInviteRepository sisInviteRepository;
    @Override
    public SisInviteLog addSisInviteLog(String mobile, Long inviterId, Long acceptId, String realName) throws Exception {
        SisInviteLog sisInviteLog=new SisInviteLog();
        sisInviteLog.setAcceptId(acceptId);
        sisInviteLog.setAcceptTime(new Date());
        sisInviteLog.setInviterId(inviterId);
        sisInviteLog.setMobile(mobile);
        sisInviteLog.setRealName(realName);
        sisInviteLog=sisInviteRepository.save(sisInviteLog);
        return sisInviteLog;
    }
}
