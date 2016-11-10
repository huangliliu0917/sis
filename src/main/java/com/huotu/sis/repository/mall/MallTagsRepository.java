package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.MallTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lqf on 2016/3/3
 * @since 1.4
 */
@Repository
public interface MallTagsRepository extends JpaRepository<MallTag, Long>, JpaSpecificationExecutor<MallTag> {

    @Query(value = "SELECT DISTINCT b.Rel_Id FROM Mall_Tags a,Mall_TagRel b WHERE a.Tag_Id = b.Tag_Id and a.Customer_Id = ?1 and a.Tag_Id in (?2)", nativeQuery = true)
    List<Long> findTagRelByTypeId(Long customerId, String tagId);

    @Query(value = "SELECT DISTINCT b.Rel_Id FROM Mall_Tags a,Mall_TagRel b WHERE a.Tag_Id = b.Tag_Id and a.Customer_Id = ?1 and a.Tag_Name like %?2", nativeQuery = true)
    List<Long> findTagRelByTypeName(Long customerId, String tagName);

    List<MallTag> findByCustomerId(Long customerId);
}
