package com.huotu.sis.service.impl;

import com.huotu.huobanplus.common.entity.Order;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.entity.UserTempIntegralHistory;
import com.huotu.sis.entity.Sis;
import com.huotu.sis.entity.SisLevel;
import com.huotu.sis.model.sis.SisSearchModel;
import com.huotu.sis.repository.SisRepository;
import com.huotu.sis.service.SisService;
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
import java.util.Objects;

/**
 * Created by Administrator on 2016/1/29.
 */
@Service
public class SisServiceImpl implements SisService {
    private static Log log = LogFactory.getLog(SisServiceImpl.class);
    @Autowired
    SisRepository sisRepository;

    @Override
    public long getSisLevelId(User user) throws Exception {
        Sis sis=sisRepository.findByUser(user);
        if(!Objects.isNull(sis)){
            SisLevel sisLevel=sis.getSisLevel();
            if(!Objects.isNull(sisLevel)){
                return sisLevel.getId();
            }
        }
        return 0;
    }

    @Override
    public Integer getSisLevel(User user) {
        Sis sis=sisRepository.findByUser(user);
        if(!Objects.isNull(sis)){
            SisLevel sisLevel=sis.getSisLevel();
            if(!Objects.isNull(sisLevel)){
                return sisLevel.getLevelNo();
            }
        }
        return null;
    }

    @Override
    public Page<Sis> findSisList(SisSearchModel sisSearchModel) throws Exception {
        log.info(sisSearchModel.getCustomerId()+"into findSisList");
        return  sisRepository.findAll(new Specification<Sis>() {
            @Override
            public Predicate toPredicate(Root<Sis> root, CriteriaQuery<?> query, CriteriaBuilder cb){

                /**
                 * 前提是属于某个商家的店中店
                 */
                Predicate predicate = cb.equal(root.get("customerId").as(Long.class),
                        sisSearchModel.getCustomerId());

                //加入标题模糊搜索
                if (!StringUtils.isEmpty(sisSearchModel.getUserLoginName())){
                    predicate = cb.and(predicate,cb.like(root.get("user").get("loginName").as(String.class),
                            "%" + sisSearchModel.getUserLoginName()+"%"));
                }
                //加入时间搜索大于
                if(!StringUtils.isEmpty(sisSearchModel.getStartTime())){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = null;
                    try {
                        date = sdf.parse(sisSearchModel.getStartTime());
                    } catch (ParseException e) {
                        throw  new RuntimeException("字符串转日期失败");
                    }
                    predicate=cb.and(predicate,cb.greaterThanOrEqualTo(root.get("openTime").as(Date.class),date));
                }
                //加入时间搜索小于
                if(!StringUtils.isEmpty(sisSearchModel.getEndTime())){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = null;
                    try {
                        date = sdf.parse(sisSearchModel.getEndTime());
                    } catch (ParseException e) {
                        throw  new RuntimeException("字符串转日期失败");
                    }
                    predicate=cb.and(predicate,cb.lessThanOrEqualTo(root.get("openTime").as(Date.class),date));
                }
                return predicate;
            }
        },new PageRequest(sisSearchModel.getPageNoStr(), 10,new Sort(Sort.Direction.DESC,"id")));
    }

    @Override
    public void calculatePushAward(User user, Order order, String unionOrderId) throws Exception {

    }

    @Override
    public void countProprietor() throws Exception {

    }

    @Override
    public void countDefPush() throws Exception {

    }

    @Override
    public UserTempIntegralHistory saveUserTempIntegralHistory() throws Exception {
        return null;
    }
}
