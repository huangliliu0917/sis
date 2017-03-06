package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.TagRel;
import com.huotu.huobanplus.common.entity.pk.TagRelPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * Created by lqf on 2016/3/4
 * @since 1.4
 */
@Repository
public interface TagRelRepository extends JpaRepository<TagRel, TagRelPK>, JpaSpecificationExecutor<TagRel> {
}
