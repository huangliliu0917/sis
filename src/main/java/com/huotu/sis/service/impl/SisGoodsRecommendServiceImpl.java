package com.huotu.sis.service.impl;

import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.User;
import com.huotu.huobanplus.common.repository.GoodsRepository;
import com.huotu.sis.entity.SisGoods;
import com.huotu.sis.common.SqlHelper;
import com.huotu.sis.service.SisGoodsRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by slt on 2016/3/8.
 */
@Service
public class SisGoodsRecommendServiceImpl implements SisGoodsRecommendService {
    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    EntityManager entityManager;


    @Override
    public Page<SisGoods> findSisRecommendGoodsModel(Long customerId, User user, String title, Pageable pageable) throws Exception {

        Query query=entityManager.createQuery(SqlHelper.getfindPageSisRecommendGoodsHql(title));
//        Query query=entityManager.createNativeQuery(SqlHelper.getfindPageSisRecommendGoodsSql(title,pageNo,pageSize));
        query.setParameter("user",user);
        query.setParameter("customerId",customerId);
        if(!StringUtils.isEmpty(title)){
            query.setParameter("title","%"+title+"%");
        }
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List list = query.getResultList();
        List<SisGoods> sisGoodses=new ArrayList<>();

        list.forEach(r -> {
            Object[] data = (Object[]) r;
            SisGoods sisGoods=new SisGoods();
            Goods goods=(Goods)data[0];
            sisGoods.setGoods(goods);
            sisGoods.setSelected(data[1]==null?false:(Boolean) data[1]);
            sisGoods.setUser(user);
            sisGoods.setMerchant(user.getMerchant());
            sisGoodses.add(sisGoods);
        });
        query=entityManager.createQuery(SqlHelper.getCountSisRecommendGoodsSql(title));
        query.setParameter("customerId",customerId);
        if(!StringUtils.isEmpty(title)){
            query.setParameter("title","%"+title+"%");
        }

        list = query.getResultList();

        return new PageImpl<>(sisGoodses, pageable,Long.parseLong(list.get(0).toString()));
    }
}
