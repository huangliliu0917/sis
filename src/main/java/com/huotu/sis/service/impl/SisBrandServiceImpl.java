package com.huotu.sis.service.impl;

import com.huotu.huobanplus.common.entity.Brand;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.SisBrand;
import com.huotu.sis.repository.SisBrandRepository;
import com.huotu.sis.repository.mall.BrandRepository;
import com.huotu.sis.service.SisBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinzj on 2016/1/25.
 */
@Service
public class SisBrandServiceImpl implements SisBrandService {

    @Autowired
    private SisBrandRepository sisBrandRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Override
    public Page<SisBrand> findByUserId(Long userId, int page, int pageSize, Sort sort) {
        Pageable pageable;
        if (null != sort)
            pageable = new PageRequest(page, pageSize, sort);
        else
            pageable = new PageRequest(page, pageSize);
        return sisBrandRepository.findAll(((root, query, cb) -> {
            Predicate predicate = cb.and(
                    cb.equal(root.get("user").get("id").as(Long.class), userId),
                    cb.isFalse(root.get("brand").get("disabled").as(Boolean.class)),
                    cb.isTrue(root.get("selected").as(Boolean.class))
            );
            return predicate;
        }), pageable);
    }

    @Override
    public Page<SisBrand> findAllByUser(User user, int page, int pageSize, Sort sort) {
        Pageable pageable;
        if (null != sort)
            pageable = new PageRequest(page, pageSize, sort);
        else
            pageable = new PageRequest(page, pageSize);

        Page<Brand> brands= brandRepository.findAll(((root, query, cb) -> {
            Predicate predicate =cb.isFalse(root.get("disabled").as(Boolean.class));
            return predicate;
        }), pageable);

        List<SisBrand> sisBrands=new ArrayList<>();
        for(Brand b:brands){
            SisBrand sisBrand=new SisBrand();
            sisBrand.setSelected(true);
            sisBrand.setUser(user);
            sisBrand.setBrand(b);
            sisBrands.add(sisBrand);
        }
        return new PageImpl<SisBrand>(sisBrands,pageable,brands.getTotalElements());
    }

    @Override
    public Page<Brand> getAllBrandByCustomerIdAndBrandName(Long customerId, String brandName, int page, int pageSize)
    {
        if (customerId == null) {
            return null;
        }
        //筛选条件
        Specification<Brand> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("customerId").as(Long.class), customerId));
            predicates.add(criteriaBuilder.isFalse(root.get("disabled")));
            predicates.add(criteriaBuilder.isTrue(root.get("sisStatus")));  //在店中店中可选
            if (brandName != null) {
                predicates.add(criteriaBuilder.like(root.get("brandName").as(String.class), "%" + brandName + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        return brandRepository.findAll(specification, new PageRequest(page-1, pageSize, new Sort(Sort.Direction.DESC, "id")));
    }

    @Override
    public List<SisBrand> getAllSisBrandList(Long userId, Long brandId)
    {
        if (brandId == null || userId == null) {
            return null;
        }
        //筛选条件
        Specification<SisBrand> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("brand").get("id").as(Long.class), brandId));
            predicates.add(criteriaBuilder.equal(root.get("user").get("id").as(Long.class), userId));
            //predicates.add(criteriaBuilder.equal(root.get("merchant").get("id").as(Long.class), merchantId));

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        return sisBrandRepository.findAll(specification);
    }

    @Override
    public Long countByCustomerId(Long userId) {
        if (userId == null) {
            return null;
        }
        //筛选条件
        Specification<SisBrand> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isFalse(root.get("brand").get("disabled")));
            predicates.add(criteriaBuilder.isTrue(root.get("brand").get("sisStatus"))); //在店中店中可选
            predicates.add(criteriaBuilder.equal(root.get("user").get("id").as(Long.class), userId));

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        return sisBrandRepository.count(specification);
    }

    @Override
    public SisBrand getOne(Long brandId, Long userId) {
        return sisBrandRepository.findOne(((root, query, cb) -> {
            Predicate predicate = cb.and(
                    cb.equal(root.get("brand").get("id").as(Long.class), brandId),
                    cb.equal(root.get("user").get("id").as(Long.class), userId),
                    cb.isFalse(root.get("brand").get("disabled").as(Boolean.class)),
                    cb.isTrue(root.get("selected").as(Boolean.class))
            );
            return predicate;
        }));
    }

    @Override
    public Integer getMaxWeight(Long userId) {
        return sisBrandRepository.getMaxWeight(userId);
    }

    @Override
    public SisBrand save(SisBrand sisBrand) {
        return sisBrandRepository.save(sisBrand);
    }

    @Override
    public void delete(SisBrand sisBrand) {
        sisBrandRepository.delete(sisBrand);
    }


}
