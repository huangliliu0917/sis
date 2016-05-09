package com.huotu.sis.repository;

import com.huotu.sis.entity.SisGoodsRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by slt on 2016/3/8.
 */
@Repository
public interface SisGoodsRecommendRepository extends JpaRepository<SisGoodsRecommend, Long>{

}
