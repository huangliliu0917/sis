package com.huotu.sis.repository;

import com.huotu.sis.entity.IndirectPushFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by Administrator on 2016/5/9.
 */
public interface IndirectPushFlowRepository extends JpaRepository<IndirectPushFlow,Long>,JpaSpecificationExecutor<IndirectPushFlow> {
}
