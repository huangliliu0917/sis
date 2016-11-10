package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.base.data.JpaSpecificationExecutor;
import com.huotu.huobanplus.common.entity.GoodsKeywords;
import com.huotu.huobanplus.model.entity.pk.GoodsKeywordsPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by jin on 2016/8/24.
 */
@Repository
public interface GoodsKeywordsRepository extends JpaRepository<GoodsKeywords,
        GoodsKeywordsPK>, JpaSpecificationExecutor<GoodsKeywords> {

    String ManagerRole = "GOODS_MANAGE";

    /**
     * 根据商品id查询关键字列表
     *
     * @param goodsId   商品id
     * @return  商品关键字列表
     */
    List<GoodsKeywords> findByPk_Goods(@Param("goodsId") Long goodsId);
}
