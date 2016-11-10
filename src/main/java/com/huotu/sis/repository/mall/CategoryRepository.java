/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.Category;
import com.huotu.huobanplus.common.entity.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author CJ
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    String ManagerRole = "CATEGORY_MANAGE";

    Page<Category> findByOwner_Id(@Param("id") Long id, Pageable pageable);

    List<Category> findByOwnerAndParentIdOrderBySortId(Merchant merchant, Long parentId);

    /**
     * 根据商户id和父类id 获取 分类列表
     *
     * @since 1.4
     */
    List<Category> findByOwner_IdAndParentId(Long ownerId, Long parentId);

    /**
     * 根据商户id和分类id 获取 该分类
     *
     * @since 1.4
     */
    Category findByOwner_IdAndId(Long ownerId, Long catId);

    /**
     * 根据商户id和分类catpath 获取 分类列表
     *
     * @since 1.4
     */
    @Query(value = "SELECT a FROM Category a WHERE a.owner.id = ?1 and a.catPath like %?2")
    List<Category> findByOwner_IdAndCatPath(Long ownerId, String catPath);

    List<Category> findByOwner(Merchant merchant);

    // 删除
    @PreAuthorize("hasAnyRole('ROOT','" + ManagerRole + "')")
    @Override
    void delete(Category entity);

    @PreAuthorize("hasAnyRole('ROOT','" + ManagerRole + "')")
    @Override
    void deleteInBatch(Iterable<Category> entities);

    @PreAuthorize("hasAnyRole('ROOT','" + ManagerRole + "')")
    @Override
    void deleteAllInBatch();

    //保存
    @PreAuthorize("hasAnyRole('ROOT','" + ManagerRole + "')")
    @Override
    <S extends Category> List<S> save(Iterable<S> entities);

    @PreAuthorize("hasAnyRole('ROOT','" + ManagerRole + "')")
    @Override
    <S extends Category> S save(S entity);

    //获取
    @Override
    Category findOne(Long aLong);

    @Override
    Page<Category> findAll(Pageable pageable);

    @Override
    Category getOne(Long aLong);

    @Override
    List<Category> findAll(Iterable<Long> longs);

    @Override
    List<Category> findAll(Sort sort);

    @Override
    List<Category> findAll();

    List<Category> findByOwner_Id(@Param("id") Long id);

//    /**
//     * 查询所有子分类
//     *
//     * @param id 当前分类id
//     * @return 所有子分类List
//     * @apiNote 蒋才添加
//     * @since 1.4
//     */
//    @PreAuthorize("hasAnyRole('ROOT','" + CommonServiceSpringConfig.MallUser + "','" + ManagerRole + "')")
//    Stream<Category> findByParentId(@Param("id") Long id);
}
