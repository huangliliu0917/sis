package com.huotu.sis.repository.mall;

import com.huotu.huobanplus.common.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * Created by jinzj on 2016/2/1.
 *
 * @since 1.3
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String>,
        JpaSpecificationExecutor<Order> {
        @Query("select o.userId from Order as o where o.id=?1")
        Integer findUserIdByOrderId(String orderId);
}
