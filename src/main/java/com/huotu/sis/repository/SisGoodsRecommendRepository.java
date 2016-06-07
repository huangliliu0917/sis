package com.huotu.sis.repository;

import com.huotu.sis.entity.SisGoodsRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by slt on 2016/3/8.
 */
public interface SisGoodsRecommendRepository extends JpaRepository<SisGoodsRecommend, Long>{
    @Modifying
    @Transactional(value = "transactionManager")
    @Query("delete from SisGoodsRecommend as sg where sg.goodsId=?1")
    void deleteGoodsByGoodsId(Long goodsId);

    @Query("select max(sg.sortNo) from SisGoodsRecommend as sg where sg.customerId=?1")
    Long findMaxSortNoByCustomerId(Long customerId);

    SisGoodsRecommend findByGoodsId(Long goodsId);

}
