package com.masai.service;

import com.masai.models.SellerOrdersNotification;

import java.util.List;

public interface SellerNotificationService {

    public int notificationCount()throws Exception;

    public List<SellerOrdersNotification> getNotification();
}
