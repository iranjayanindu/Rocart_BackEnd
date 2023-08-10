package com.masai.service;

import com.masai.models.NotificationStatus;
import com.masai.models.SellerOrdersNotification;
import com.masai.repository.SellerNotificationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerNotificationServiceImpl implements SellerNotificationService{

    @Autowired
    private SellerNotificationDao sellerNotificationDao;

    @Override
    public int notificationCount() throws Exception {
        String notread = String.valueOf(NotificationStatus.NOTREAD);
        int notificationCount = sellerNotificationDao.getNotificationCount(notread);
        return notificationCount;
    }

    @Override
    public List<SellerOrdersNotification> getNotification() {
        String notread = String.valueOf(NotificationStatus.NOTREAD);
        List<SellerOrdersNotification> notification = sellerNotificationDao.getNotification(notread);
        return notification;
    }
}
