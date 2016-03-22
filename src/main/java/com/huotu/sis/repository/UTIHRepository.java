package com.huotu.sis.repository;


import com.huotu.huobanplus.common.entity.UserTempIntegralHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by admin on 2016/1/31.
 */
@RepositoryRestResource
public interface UTIHRepository extends JpaRepository<UserTempIntegralHistory,String>
{



}
