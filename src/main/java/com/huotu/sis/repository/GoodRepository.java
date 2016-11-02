package com.huotu.sis.repository;

import com.huotu.huobanplus.common.entity.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by slt on 2016/3/8.
 */
@Repository
public interface GoodRepository extends JpaRepository<Goods, Long>, JpaSpecificationExecutor<Goods> {

    @Query("select g from Goods as g inner join SisGoodsRecommend as sg on " +
            "g.id=sg.goodsId where sg.customerId=?1 order by sg.sortNo desc ,sg.id desc ")
    Page<Goods> findSisGoodsRecommendList(Long customerId, Pageable pageable);

    @Query("select g.id,g.title,g.price,g.stock,sg.id from Goods as g left join SisGoodsRecommend as sg " +
            "on g.id=sg.goodsId where g.owner.id=?1 and g.title like ?2" +
            " and g.disabled=false and g.marketable=true and g.scenes=0 and g.saleChannels in(0,2)")
    Page findByOwnerAndTitleLike(Long merchantId, String title, Pageable pageable);

}
