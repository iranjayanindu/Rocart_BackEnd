package com.masai.repository;

import com.masai.models.Customer;
import com.masai.models.SellerOrdersNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SellerNotificationDao extends JpaRepository<SellerOrdersNotification,Integer> {

    @Query(value = "SELECT COUNT(*) AS notification_count FROM seller_orders_notification WHERE notification_status = :type && seller_id = :id",nativeQuery = true)
    int getNotificationCount(@Param("type")String type,@Param("id")String id);

    @Query(value = "SELECT * FROM seller_orders_notification WHERE notification_status = :type && seller_id = :id ORDER BY date_create DESC",nativeQuery = true)
    List<SellerOrdersNotification> getNotification(@Param("type")String type,@Param("id")String id);

    @Query(value = "SELECT * FROM seller_orders_notification where seller_id = :id ORDER BY date_create DESC", nativeQuery = true)
    List<SellerOrdersNotification> getAllOrdersBySeller(@Param("id")String id);

    @Query(value = "SELECT COUNT(*) AS notification_count FROM seller_orders_notification WHERE order_status = :type && seller_id = :id",nativeQuery = true)
    int getSellerOrderStatusCount(@Param("type")String type,@Param("id")String id);

    @Query(value = "SELECT * FROM seller_orders_notification where order_status = :type && seller_id = :id ORDER BY date_create DESC", nativeQuery = true)
    List<SellerOrdersNotification> getAllOrdersByStatus(@Param("id")String id,@Param("type")String type);
}
