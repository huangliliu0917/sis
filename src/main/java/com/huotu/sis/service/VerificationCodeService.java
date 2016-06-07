package com.huotu.sis.service;

import com.huotu.sis.entity.VerificationCode;
import com.huotu.sis.model.sis.SisSearchCodeModel;
import org.springframework.data.domain.Page;

/**
 * Created by Administrator on 2016/3/2.
 */
public interface VerificationCodeService {

    /**
     * 查找验证码表
     * @param sisSearchCodeModel    查找的model
     * @return
     * @throws Exception
     */
    Page<VerificationCode> findSisCodes(SisSearchCodeModel sisSearchCodeModel) throws Exception;
}
