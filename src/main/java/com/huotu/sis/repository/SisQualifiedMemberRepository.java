package com.huotu.sis.repository;

import com.huotu.sis.entity.SisQualifiedMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by slt on 2016/2/3.
 */
@Repository
public interface SisQualifiedMemberRepository extends JpaRepository<SisQualifiedMember,Long> {
    Long countByMemberId(Long memberId);

}
