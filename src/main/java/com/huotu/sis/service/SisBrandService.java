package com.huotu.sis.service;

import com.huotu.huobanplus.common.entity.Brand;
import com.huotu.sis.entity.SisBrand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Created by jinzj on 2016/1/25.
 * @since 1.3
 */
public interface SisBrandService {

    /**
     * 得到某店主已选择的品牌,分页排序
     *
     * @param userId
     * @param page
     * @param pageSize
     * @param sort
     * @return
     */
    Page<SisBrand> findByUserId(Long userId, int page, int pageSize, Sort sort);

    /**
     * 某商户所有的品牌，分页
     * @param customerId
     * @param brandName
     * @param page
     * @param pageSize
     * @return
     */
    Page<Brand> getAllBrandByCustomerIdAndBrandName(Long customerId, String brandName, int page, int pageSize);

    /**
     * 该用户店中店的商品
     * @param userId
     * @param brandId
     * @return
     */
    List<SisBrand> getAllSisBrandList(Long userId, Long brandId);

    /**
     * 该商家所有已上架的品牌的数量
     *
     * @param userId
     * @return
     */
    Long countByCustomerId(Long userId);

    /**
     * 根据品牌id得到详情
     *
     * @param brandId
     * @param userId
     * @return
     */
    SisBrand getOne(Long brandId, Long userId);

    /**
     * 店中店品牌权重最大值
     *
     * @param userId
     * @return
     */
    Integer getMaxWeight(Long userId);

    /**
     * 保存店中店品牌
     *
     * @param sisBrand
     * @return
     */
    SisBrand save(SisBrand sisBrand);

    void delete(SisBrand sisBrand);
}
