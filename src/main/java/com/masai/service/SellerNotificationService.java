package com.masai.service;

import com.masai.models.SellerOrdersNotification;

import java.util.List;

public interface SellerNotificationService {

    public int notificationCount(String token)throws Exception;

    public List<SellerOrdersNotification> getNotification(String token);

    public List<SellerOrdersNotification> getAllOrdersByCustomer(String token);

    public SellerOrdersNotification updateOrderNotification(int sellerId);

    public SellerOrdersNotification updateOrderDilevery(int notificationId, int i);
}
