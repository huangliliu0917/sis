package com.huotu.sis.repository;

import com.huotu.sis.entity.SisQualifiedMember;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by slt on 2016/2/3.
 */
public interface SisQualifiedMemberRepository extends JpaRepository<SisQualifiedMember,Long> {
    Long countByMemberId(Long memberId);

}
