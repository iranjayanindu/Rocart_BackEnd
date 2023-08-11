package com.masai.service;

import com.masai.models.NotificationStatus;
import com.masai.models.OrderStatusValues;
import com.masai.models.Seller;
import com.masai.models.SellerOrdersNotification;
import com.masai.repository.SellerDao;
import com.masai.repository.SellerNotificationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SellerNotificationServiceImpl implements SellerNotificationService{

    @Autowired
    private SellerNotificationDao sellerNotificationDao;

    @Autowired
    private SellerService sService;

    @Autowired
    private SellerDao sellerDao;

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

    @Override
    public SellerOrdersNotification updateOrderNotification(int id) {
        Optional<SellerOrdersNotification> byId = sellerNotificationDao.findById(id);
        SellerOrdersNotification sellerOrdersNotification = byId.get();
        sellerOrdersNotification.setNotificationStatus(NotificationStatus.READ);
        return sellerNotificationDao.save(sellerOrdersNotification);
    }

    @Override
    public SellerOrdersNotification updateOrderDilevery(int notificationId, int i) {
        OrderStatusValues preparing;
        switch (i){
            case 1:
                preparing = OrderStatusValues.PREPARING;
                break;
            case 2:
                preparing = OrderStatusValues.SEND;
                break;
            case 3:
                preparing = OrderStatusValues.DELIVERED;
                break;
            default:
                preparing = OrderStatusValues.NEW;
        }
        Optional<SellerOrdersNotification> byId = sellerNotificationDao.findById(notificationId);
        SellerOrdersNotification sellerOrdersNotification = byId.get();
        sellerOrdersNotification.setOrderStatus(preparing);
        sellerNotificationDao.save(sellerOrdersNotification);
        return null;
    }
}
