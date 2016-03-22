package com.huotu.sis.repository;

import com.huotu.huobanplus.common.entity.User;
import com.huotu.sis.entity.SisApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lgh on 2015/11/9.
 */

@Repository
public interface SisApplyRepository extends JpaRepository<SisApply, Long> {

    @Query("select apply from SisApply apply where apply.user=?1 order by apply.id desc")
    List<SisApply> findByUser(User user);
}
