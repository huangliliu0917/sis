package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by helloztt on 2016/7/12.
 *
 * @since 1.5.1
 */
@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItems, Integer> {


    OrderItems findTop1ByOrder_Id(String orderId);

    List<OrderItems> findAllByOrder_Id(String orderId);

    @Query("SELECT oi FROM OrderItems oi WHERE oi.order.id = ?1")
    List<OrderItems> getOrderItemsByOrderId(String orderId);
}
