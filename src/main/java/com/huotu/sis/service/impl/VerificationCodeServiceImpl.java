package com.huotu.sis.service.impl;

import com.huotu.sis.entity.VerificationCode;
import com.huotu.sis.model.sis.SisSearchCodeModel;
import com.huotu.sis.repository.VerificationCodeRepository;
import com.huotu.sis.service.VerificationCodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/3/2.
 */
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {
    private static Log log = LogFactory.getLog(VerificationCodeServiceImpl.class);

    @Autowired
    VerificationCodeRepository verificationCodeRepository;
    @Override
    public Page<VerificationCode> findSisCodes(SisSearchCodeModel sisSearchCodeModel) throws Exception {
        return  verificationCodeRepository.findAll(new Specification<VerificationCode>() {
            @Override
            public Predicate toPredicate(Root<VerificationCode> root, CriteriaQuery<?> query, CriteriaBuilder cb){

                /**
                 * 前提是属于某个商家的店中店
                 */
                Predicate predicate = cb.equal(root.get("customerId").as(Long.class),
                        sisSearchCodeModel.getMerchantId());

                //加入手机号模糊搜索
                if (!StringUtils.isEmpty(sisSearchCodeModel.getMobile())){
                    predicate = cb.and(predicate,cb.like(root.get("mobile").as(String.class),
                            "%" + sisSearchCodeModel.getMobile()+"%"));
                }
                //加入时间搜索大于
                if(!StringUtils.isEmpty(sisSearchCodeModel.getStartTime())){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = null;
                    try {
                        date = sdf.parse(sisSearchCodeModel.getStartTime());
                    } catch (ParseException e) {
                        throw  new RuntimeException("字符串转日期失败");
                    }
                    predicate=cb.and(predicate,cb.greaterThanOrEqualTo(root.get("openTime").as(Date.class),date));
                }
                //加入时间搜索小于
                if(!StringUtils.isEmpty(sisSearchCodeModel.getEndTime())){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = null;
                    try {
                        date = sdf.parse(sisSearchCodeModel.getEndTime());
                    } catch (ParseException e) {
                        throw  new RuntimeException("字符串转日期失败");
                    }
                    predicate=cb.and(predicate,cb.lessThanOrEqualTo(root.get("openTime").as(Date.class),date));
                }
                return predicate;
            }
        },new PageRequest(sisSearchCodeModel.getPageNoStr(), 10,new Sort(Sort.Direction.DESC,"sendTime")));
    }
}
