package com.masai.service;

import com.masai.models.SellerOrdersNotification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SellerNotificationService {

    public int notificationCount(String token)throws Exception;

    public int sellerOrderStatuesCount(String token,int status)throws Exception;



    public List<SellerOrdersNotification> getNotification(String token);

    public List<SellerOrdersNotification> getAllOrdersByCustomer(String token);

    public List<SellerOrdersNotification> getAllOrdersByStatus(String token,int status);

    public SellerOrdersNotification updateOrderNotification(int sellerId);

    public SellerOrdersNotification updateOrderDilevery(int notificationId, int i);


}
