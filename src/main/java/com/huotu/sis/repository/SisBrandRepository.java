package com.huotu.sis.repository;

import com.huotu.sis.entity.SisBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.Description;

/**
 * Created by jinzj on 2016/1/25.
 */
public interface SisBrandRepository extends JpaRepository<SisBrand, Long>, JpaSpecificationExecutor<SisBrand> {

    @Description("获取小伙伴店中店品牌最大的权重值")
    @Query("select max(sis.orderWeight) from SisBrand as sis where sis.user.id=:userId")
    int getMaxWeight(@Param("userId") Long userId);
}
