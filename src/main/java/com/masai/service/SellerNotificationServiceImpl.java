package com.masai.service;

import com.masai.models.NotificationStatus;
import com.masai.models.Seller;
import com.masai.models.SellerOrdersNotification;
import com.masai.repository.SellerNotificationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerNotificationServiceImpl implements SellerNotificationService{

    @Autowired
    private SellerNotificationDao sellerNotificationDao;

    @Autowired
    private SellerService sService;

    @Override
    public int notificationCount(String token) throws Exception {
        Seller seller1 = sService.getCurrentlyLoggedInSeller(token);
        String sellerId = String.valueOf(seller1.getSellerId());
        String notRead = String.valueOf(NotificationStatus.NOTREAD);
        return sellerNotificationDao.getNotificationCount(notRead,sellerId);
    }

    @Override
    public List<SellerOrdersNotification> getNotification(String token) {
        Seller seller1 = sService.getCurrentlyLoggedInSeller(token);
        String sellerId = String.valueOf(seller1.getSellerId());
        String notRead = String.valueOf(NotificationStatus.NOTREAD);
        return sellerNotificationDao.getNotification(notRead,sellerId);
    }

    @Override
    public List<SellerOrdersNotification> getAllOrdersByCustomer(String token) {
        Seller seller1 = sService.getCurrentlyLoggedInSeller(token);
        String sellerId = String.valueOf(seller1.getSellerId());
        return sellerNotificationDao.getAllOrdersBySeller(sellerId);
    }
}
