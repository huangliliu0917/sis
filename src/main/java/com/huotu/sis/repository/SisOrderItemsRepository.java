package com.huotu.sis.repository;

import com.huotu.huobanplus.common.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by admin on 2016/1/31.
 */
public interface SisOrderItemsRepository extends JpaRepository<OrderItems, String>
{
    @Query("SELECT oi FROM OrderItems oi WHERE oi.order.id = ?1")
    List<OrderItems> getOrderItemsByOrderId(String orderId);

}
