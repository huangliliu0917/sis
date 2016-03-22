package com.huotu.sis.repository;

import com.huotu.huobanplus.common.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by admin on 2016/1/29.
 */
@Repository
public interface SisOrderRepository extends JpaRepository<Order, String>
{
}
