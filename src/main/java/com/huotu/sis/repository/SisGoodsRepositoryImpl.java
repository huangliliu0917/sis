/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.repository;

import com.huotu.huobanplus.common.entity.Brand;
import com.huotu.huobanplus.common.entity.Category;
import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.SisGoods;
import com.huotu.sis.repository.mall.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CJ
 */
@Component
public class SisGoodsRepositoryImpl implements SisGoodsRepositoryCustom {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private SisGoodsRepository sisGoodsRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Page<SisGoods> findUserAllSISGoods(User user, Pageable pageable) {
        Page page = sisGoodsRepository.findAllSISGoods(user, user.getMerchant(), pageable);

        List list = page.getContent();
        List<SisGoods> result = new ArrayList<>();
        //noinspection unchecked
        list.forEach(object -> {
            Object[] data = (Object[]) object;
            if (data.length == 2 && data[1] != null) {
                result.add((SisGoods) data[1]);
            } else {
                SisGoods goods = new SisGoods();
                goods.setGoods((Goods) data[0]);
                goods.setUser(user);
                result.add(goods);
            }
        });

        return new PageImpl<>(result, pageable, page.getTotalElements());
    }

    @Override
    public List<SisGoods> findUserSISGoods(User user, Long catId, String key, Pageable pageable) {
        Category category = categoryRepository.findOne(catId);


        StringBuffer hql = new StringBuffer();
        hql.append("select  goods,sis from Goods as goods ");
        if (category != null) {
            hql.append(" inner join Category as cat on goods.category=cat ");
        }
        hql.append(" left join SisGoods as sis on sis.goods=goods and sis.user=:user ");
        hql.append(" where goods.owner=:merchant " +
                        " and goods.scenes=0 " +
                        " and goods.marketable=true " +
                        " and goods.category.id!=0 " +
                        " and goods.disabled=false " +
                        " and (sis.deleted=false or sis is null) "
        );

        if (!StringUtils.isEmpty(key)) {
            hql.append(" and goods.title like :key ");
        }
        if (category != null) {
            hql.append(" and cat.catPath like :catpath ");
        }

        hql.append(" order by goods.orderWeight desc,goods.id desc");
        Query query = entityManager.createQuery(hql.toString());
        if (category != null) {
            query.setParameter("catpath", category.getCatPath() + "%");
//            query.setParameter("cat",category);
        }
        if (!StringUtils.isEmpty(key)) {
            query.setParameter("key", "%" + key + "%");
        }
        query.setParameter("merchant", user.getMerchant());
        query.setParameter("user", user);
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List list = query.getResultList();
        List<SisGoods> result = new ArrayList<>();
        list.forEach(object -> {
            Object[] data = (Object[]) object;
            if (data.length == 2 && data[1] != null) {
                result.add((SisGoods) data[1]);
            } else {
                SisGoods goods = new SisGoods();
                goods.setGoods((Goods) data[0]);
                goods.setUser(user);
                result.add(goods);
            }
        });
        return result;
    }

    @Override
    public Integer findUserSISGoodsCount(User user, Long categoryId, String name) {
        Category category = null;
        if (null != categoryId) {
            category = categoryRepository.findOne(categoryId);
        }


        StringBuffer hql = new StringBuffer();
        hql.append("select  goods,sis from Goods as goods ");
        if (category != null) {
            hql.append(" inner join Category as cat on goods.category=cat ");
        }
        hql.append(" left join SisGoods as sis on sis.goods=goods ");
        hql.append(" where goods.owner=:merchant " +
                        " and goods.scenes=0 " +
                        " and goods.marketable=true " +
                        " and goods.category.id!=0 " +
                        " and goods.disabled=false " +
                        " and (sis is null or (sis.deleted=false and sis.user=:user)) "
        );

        if (!StringUtils.isEmpty(name)) {
            hql.append(" and goods.title like :key ");
        }
        if (category != null) {
            hql.append(" and cat.catPath like :catpath ");
        }

        hql.append(" order by goods.id desc");
        Query query = entityManager.createQuery(hql.toString());
        if (category != null) {
            query.setParameter("catpath", category.getCatPath() + "%");
//            query.setParameter("cat",category);
        }
        if (!StringUtils.isEmpty(name)) {
            query.setParameter("key", "%" + name + "%");
        }
        query.setParameter("merchant", user.getMerchant());
        query.setParameter("user", user);
        List list = query.getResultList();
        return list.size();
    }

    @Override
    public Integer findUserSISGoodsCount(User user, String keywords, Brand brand) {
        StringBuilder hql = new StringBuilder();
        hql.append("SELECT goods,sis FROM Goods AS goods ");
        /*if (null != brand) {
            hql.append("INNER JOIN Brand AS brandTemp ON goods.brand=brandTemp ");
        }*/
        hql.append("LEFT JOIN SisGoods AS sis ON sis.goods=goods  ");
        hql.append("WHERE goods.owner=:merchant " +
                " AND goods.scenes=0 " +
                " AND goods.marketable=true " +
                " AND goods.category.id!=0 " +
                " AND goods.disabled=false " +
                " AND (sis is null or (sis.deleted=false and sis.user=:user))");
        if (null != brand) {
            hql.append(" AND goods.brand.id=:brandId ");
        }
        if (!StringUtils.isEmpty(keywords)) {
            hql.append("AND goods.title like :key ");
        }
        //
        Query query = entityManager.createQuery(hql.toString());
        if (null != brand) {
            query.setParameter("brandId", brand.getId());
        }
        query.setParameter("merchant", user.getMerchant());
        query.setParameter("user", user);
        if (!StringUtils.isEmpty(keywords)) {
            query.setParameter("key", "%" + keywords + "%");
        }
        //
        List list = query.getResultList();
        return list.size();
    }

    @Override
    public List<Goods> findSISGoods(User user, boolean selected, String key, Pageable pageable) {
        StringBuffer hql = new StringBuffer();
        hql.append("select goods from Goods as goods " +
                " left join SisGoods as sis on sis.goods=goods and sis.user=:user " +
                " where goods.marketable=true " +
                " and goods.disabled=false " +
                " and sis.selected=:selected " +
                " and (sis.deleted=false or sis is null) ");
        if (!StringUtils.isEmpty(key)) {
            hql.append(" and goods.title like :key ");
        }
        hql.append("order by sis.orderWeight desc,sis.id");
        Query query = entityManager.createQuery(hql.toString());
        query.setParameter("user", user);
        query.setParameter("selected", selected);
        if (!StringUtils.isEmpty(key)) {
            query.setParameter("key", "%" + key + "%");
        }
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List list = query.getResultList();
        List<Goods> result = new ArrayList<>();
        for (Object o : list) {
            Object data = (Object) o;
            if (data != null) {
                result.add((Goods) data);
            }
        }


        return result;
    }

    @Override
    public Long[] sisGoodsTotals(User user) {
        StringBuffer hql = new StringBuffer();
        hql.append("select count(goods) from Goods as goods " +
                " left join SisGoods as sis on sis.goods=goods and sis.user=:user " +
                " where goods.marketable=true " +
                " and goods.disabled=false " +
                " and sis.selected=:selected " +
                " and (sis.deleted=false or sis is null) ");
        Query query = entityManager.createQuery(hql.toString());
        query.setParameter("user", user);
        query.setParameter("selected", true);
        List listUp = query.getResultList();

        query = entityManager.createQuery(hql.toString());
        query.setParameter("user", user);
        query.setParameter("selected", false);
        List listOut = query.getResultList();
        Long[] totals = new Long[2];
        totals[0] = listUp.get(0) == null ? 0 : (Long) listUp.get(0);
        totals[1] = listOut.get(0) == null ? 0 : (Long) listOut.get(0);
        return totals;
    }

    @Override
    public List<SisGoods> findUserSISGoodsOrderById(User user,
                                                    Long categoryId,
                                                    String name,
                                                    Pageable pageable) {
        Category category = null;
        if (null != categoryId) {
            category = categoryRepository.findOne(categoryId);
        }


        StringBuffer hql = new StringBuffer();
        hql.append("select  goods,sis from Goods as goods ");
        if (category != null) {
            hql.append(" inner join Category as cat on goods.category=cat ");
        }
        hql.append(" left join SisGoods as sis on sis.goods=goods and sis.user=:user ");
        hql.append(" where goods.owner=:merchant " +
                        " and goods.scenes=0 " +
                        " and goods.marketable=true " +
                        " and goods.category.id!=0 " +
                        " and goods.disabled=false " +
                        " and (sis.deleted=false or sis is null) "
        );

        if (!StringUtils.isEmpty(name)) {
            hql.append(" and goods.title like :key ");
        }
        if (category != null) {
            hql.append(" and cat.catPath like :catpath ");
        }

        hql.append(" order by goods.id desc");
        Query query = entityManager.createQuery(hql.toString());
        if (category != null) {
            query.setParameter("catpath", category.getCatPath() + "%");
//            query.setParameter("cat",category);
        }
        if (!StringUtils.isEmpty(name)) {
            query.setParameter("key", "%" + name + "%");
        }
        query.setParameter("merchant", user.getMerchant());
        query.setParameter("user", user);
        if(null!=pageable){
            query.setFirstResult(pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }

        List list = query.getResultList();
        List<SisGoods> result = new ArrayList<>();
        list.forEach(object -> {
            Object[] data = (Object[]) object;
            if (data.length == 2 && data[1] != null) {
                result.add((SisGoods) data[1]);
            } else {
                SisGoods goods = new SisGoods();
                goods.setGoods((Goods) data[0]);
                goods.setUser(user);
                result.add(goods);
            }
        });
        return result;
    }

    //    @Override
    public Page<SisGoods> testMethod2(User user, Pageable pageable) {
        System.out.println(entityManager);
        System.out.println("aaa");
        Query query = entityManager.createQuery("select goods,sis from Goods as goods " +
                "left join SisGoods as sis on sis.goods=goods and sis.user=:user " +
                "where goods.owner=:merchant and (sis.deleted=false or sis is null)");

        query.setParameter("merchant", user.getMerchant());
        query.setParameter("user", user);
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List list = query.getResultList();
        List<SisGoods> result = new ArrayList<>();
        //noinspection unchecked
        list.forEach(object -> {
            Object[] data = (Object[]) object;
            if (data.length == 2 && data[1] != null) {
                result.add((SisGoods) data[1]);
            } else {
                SisGoods goods = new SisGoods();
                goods.setGoods((Goods) data[0]);
                goods.setUser(user);
                result.add(goods);
            }
        });

        return new PageImpl<>(result);
    }
}
