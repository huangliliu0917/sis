package com.huotu.sis.service;

import com.huotu.sis.entity.SisInviteLog;

/**
 * Created by slt on 2016/2/3.
 */
public interface SisInviteLogService {
    /**
     * 添加一条邀请记录
     * @param mobile        接受邀请会员手机号
     * @param inviterId     邀请人会员Id
     * @param acceptId      接受邀请会员Id
     * @param realName      接受邀请会员真实姓名
     * @return
     * @throws Exception
     */
    SisInviteLog addSisInviteLog(String mobile, Long inviterId, Long acceptId, String realName) throws Exception;
}
