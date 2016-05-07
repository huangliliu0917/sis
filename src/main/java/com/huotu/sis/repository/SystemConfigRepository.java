package com.huotu.sis.repository;

import com.huotu.sis.entity.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by lgh on 2016/5/7.
 */
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig,String> {
}
