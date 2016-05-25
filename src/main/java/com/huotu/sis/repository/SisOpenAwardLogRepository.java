package com.huotu.sis.repository;

import com.huotu.sis.entity.SisOpenAwardLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by slt on 2015/11/9.
 */

@Repository
public interface SisOpenAwardLogRepository extends JpaRepository<SisOpenAwardLog, Long> {

    List<SisOpenAwardLog> findByOrderId(String orderId);

    @Query("select s from SisOpenAwardLog as s where s.posMonthTag is null")
    List<SisOpenAwardLog> findByPosMonthTagIsNull();
}
