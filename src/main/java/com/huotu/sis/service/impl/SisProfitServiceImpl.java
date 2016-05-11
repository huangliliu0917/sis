package com.huotu.sis.service.impl;

import com.huotu.sis.entity.SISProfit;
import com.huotu.sis.repository.SisProfitRepository;
import com.huotu.sis.service.SisProfitService;
import org.jboss.logging.Cause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * Created by jinzj on 2016/5/10.
 */
@Service
public class SisProfitServiceImpl implements SisProfitService {

    @Autowired
    private SisProfitRepository sisProfitRepository;

    @Override
    public List<SISProfit> findAllByUserLevelId(Long userLevelId, Long customerId, Long sisLevelId) {
        return sisProfitRepository.findAll(((root, query, cb) -> {
            Predicate predicate = cb.and(
                    cb.equal(root.get("merchant").get("id").as(Long.class), customerId),
                    cb.equal(root.get("userLevel").as(Long.class), userLevelId)
            );
            if (null != sisLevelId) {
                predicate = cb.and(predicate, cb.equal(root.get("level").get("id").as(Long.class), sisLevelId));
            }
            return predicate;
        }));
    }
}
