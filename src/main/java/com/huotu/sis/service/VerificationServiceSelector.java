package com.huotu.sis.service;

/**
 * Created by Administrator on 2016/2/27.
 */
public interface VerificationServiceSelector {

    /**
     * 获取验证服务
     *
     * @param merchantId 商户id
     * @return 验证服务
     */
    VerificationService forMerchant(Long merchantId);


}
