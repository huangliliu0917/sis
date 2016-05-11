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

//            goods.setId(data[1]==null?0:Long.parseLong(data[1].toString()));
//            goods.setTitle(data[2]==null?"":data[2].toString());
//            goods.setSmallPic(data[3]==null?"":data[3].toString());
//            goods.setStock(data[4]==null?0:Integer.parseInt(data[4].toString()));
//            goods.setPrice(data[5]==null?0:Double.parseDouble(data[5].toString()));
//            goods.setPricesCache(data[6]==null?null:levelPricesConverter.convertToEntityAttribute(data[6].toString()));
//            goods.setShopRebateMin(data[7]==null?0:Double.parseDouble(data[7].toString()));
//            goods.setIndividuation(data[8]==null?false:(Boolean)data[8]);
//            goods.setRebateConfiguration(data[9]==null?null:productSpecificationsConverter.convertToEntityAttribute(data[9].toString()));
//            sisGoods.setGoods(goods);
//            sisGoods.setSelected(data[10]==null?false:(Boolean)data[10]);
//            sisGoods.setUser(user);
//            sisGoodses.add(sisGoods);
//            if (data.length == 2 && data[1] != null) {
//                sisGoodses.add((SisGoods) data[1]);
//            } else {
//                SisGoods goods = new SisGoods();
//                goods.setGoods((Goods) data[0]);
//                goods.setUser(user);
//                sisGoodses.add(goods);
//            }
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
