package com.huotu.sis.service.impl;

import com.huotu.huobanplus.base.data.MutableSpecification;
import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.Merchant;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.repository.GoodsRepository;
import com.huotu.sis.entity.SisConfig;
import com.huotu.sis.entity.SisGoods;
import com.huotu.sis.repository.SisConfigRepository;
import com.huotu.sis.repository.SisGoodsRepository;
import com.huotu.sis.service.SisGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/18.
 */
@Service
public class SisGoodsServiceImpl implements SisGoodsService {

    @Autowired
    private SisGoodsRepository sisGoodsRepository;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private SisConfigRepository sisConfigRepository;



    @Override
    public Page<SisGoods> getSisGoodsList(Long customerId, Long userId, int page, int pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "orderWeight");
        Pageable pageable = new PageRequest(page, pageSize, sort);
        return sisGoodsRepository.findAll(((root, query, cb) -> {
            Predicate predicate = cb.and(
//                    cb.or(cb.equal(root.get("merchant").get("id").as(Long.class), customerId),
//                            cb.isNull(root.get("merchant").get("id").as(Long.class))),
                    cb.equal(root.get("user").get("id").as(Long.class), userId),
                    cb.isTrue(root.get("goods").get("marketable").as(Boolean.class)),
                    cb.isFalse(root.get("goods").get("disabled").as(Boolean.class)),
                    cb.isTrue(root.get("selected").as(Boolean.class))
            );
            return predicate;
        }), pageable);
    }

    @Override
    public List<SisGoods> getSisGoodsList(Long customerId, Long userId) {
        return sisGoodsRepository.findAll(((root, query, cb) -> {
            Predicate predicate = cb.and(
                    cb.equal(root.get("merchant").get("id").as(Long.class), customerId),
                    cb.equal(root.get("user").get("id").as(Long.class), userId),
                    cb.isTrue(root.get("goods").get("marketable").as(Boolean.class)),
                    cb.isFalse(root.get("goods").get("disabled").as(Boolean.class)),
                    cb.isTrue(root.get("selected").as(Boolean.class))
            );
            return predicate;
        }));
    }

    @Override
    public Long countByUserId(Long customerId, Long userId) {
        return sisGoodsRepository.count(((root, query, cb) -> {
            Predicate predicate = cb.and(
                    cb.equal(root.get("merchant").get("id").as(Long.class), customerId),
                    cb.equal(root.get("user").get("id").as(Long.class), userId),
                    cb.isTrue(root.get("goods").get("marketable").as(Boolean.class)),
                    cb.isFalse(root.get("goods").get("disabled").as(Boolean.class)),
                    cb.isTrue(root.get("selected").as(Boolean.class))
            );
            return predicate;
        }));

    }

    @Override
    public Page<Goods> getAllGoodsByCustomerIdAndTitleAndCatPath(Long merchantId , String title, Long catPath, Long brandId,
                                                                 Integer sortOption, int page, int pageSize)
    {
        if (merchantId == null) {
            return null;
        }
        SisConfig sisConfig=sisConfigRepository.findByMerchantId(merchantId);
        //筛选条件
        Specification<Goods> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("owner").get("id").as(Long.class), merchantId));
            predicates.add(criteriaBuilder.equal(root.get("scenes").as(Integer.class), 0));
            predicates.add(criteriaBuilder.isFalse(root.get("disabled")));
            predicates.add(criteriaBuilder.isTrue(root.get("marketable")));
            if(sisConfig.getGoodSelectMode()!=null&&sisConfig.getGoodSelectMode()==1){
                predicates.add(criteriaBuilder.greaterThan(root.get("shopRebateMax").as(Double.class),0.0));
            }
            if (title != null) {
                predicates.add(criteriaBuilder.like(root.get("title").as(String.class), "%" + title + "%"));
            }
            if (catPath != null) {
                predicates.add(criteriaBuilder.like(root.get("category").get("catPath").as(String.class), "%|" + catPath + "|%"));
            } else {    //将分类为0的过滤掉(分类id为空可能为开店商品)
                predicates.add(criteriaBuilder.notEqual(root.get("category").get("id").as(Long.class),0));
            }
            if (brandId != null) {
                predicates.add(criteriaBuilder.equal(root.get("brand").get("id").as(Long.class), brandId));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        //排序
        Sort sort;
        if (sortOption != null){
            switch (sortOption) {
                case 0:
                    sort = new Sort(Sort.Direction.DESC, "shopRebateMin", "id");
                    break;
                case 1:
                    sort = new Sort(Sort.Direction.DESC, "moods", "id");
                    break;
                case 2:
                    sort = new Sort(Sort.Direction.DESC, "autoMarketDate", "id");
                    break;
                default:
                    sort = new Sort(Sort.Direction.DESC, "id");
                    break;
            }
        }
        else {
            sort = new Sort(Sort.Direction.DESC, "id");
        }

        return goodsRepository.findAll(specification, new PageRequest(page-1, pageSize, sort));
    }

    @Override
    public List<SisGoods> getAllSisGoodsList(Long goodsId, Long userId, Long merchantId)
    {
        if (goodsId == null || userId == null || merchantId == null) {
            return null;
        }
        //筛选条件
        Specification<SisGoods> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("goods").get("id").as(Long.class), goodsId));
            predicates.add(criteriaBuilder.equal(root.get("user").get("id").as(Long.class), userId));
//            predicates.add(criteriaBuilder.equal(root.get("merchant").get("id").as(Long.class), merchantId));
            predicates.add(criteriaBuilder.isFalse(root.get("deleted")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        return sisGoodsRepository.findAll(specification);
    }

    @Override
    public Page<SisGoods> getMallGoods(Merchant merchant, User user, int page, int pageSize) {
        Sort sort= new Sort(Sort.Direction.DESC, "autoMarketDate");
        Pageable pageable=new PageRequest(page,pageSize,sort);
        Page<Goods> goodses=goodsRepository.findAll(new MutableSpecification<Goods>() {
            @Override
            public Predicate toPredicate(Root<Goods> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.and(
                    cb.equal(root.get("owner").as(Merchant.class), merchant),
                    cb.isTrue(root.get("marketable").as(Boolean.class)),
                    cb.isFalse(root.get("disabled").as(Boolean.class))
                );
            }
            @Override
            public void beforeQuery(TypedQuery query) {

            }
        },pageable);
        List<SisGoods> sisGoodses=new ArrayList<>();
        for(Goods g:goodses){
            SisGoods sisGoods=new SisGoods();
            sisGoods.setDeleted(false);
            sisGoods.setGoods(g);
            sisGoods.setSelected(true);
            sisGoods.setMerchant(merchant);
            sisGoods.setUser(user);
            sisGoodses.add(sisGoods);
        }
        return new PageImpl<SisGoods>(sisGoodses,pageable,goodses.getTotalElements());
    }

}
