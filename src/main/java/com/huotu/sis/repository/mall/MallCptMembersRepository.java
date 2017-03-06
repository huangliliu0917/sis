package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.MallCptMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by slt on 2016/2/1.
 *
 * @since 1.3
 */
@Repository
public interface MallCptMembersRepository extends JpaRepository<MallCptMembers, Long> {
    Long countByMemberId(Long memberId);
}
