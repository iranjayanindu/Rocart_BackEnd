package com.masai.controller;

import com.masai.models.SellerOrdersNotification;
import com.masai.service.SellerNotificationService;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NotificationController {

    @Autowired
    private SellerNotificationService sellerNotificationService;

    @GetMapping("/notification/count")
    public ResponseEntity<Integer> getNotificationCount(@RequestHeader String token) throws Exception{
        Integer i = sellerNotificationService.notificationCount(token);
        return new ResponseEntity<>(i, HttpStatus.OK);
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<SellerOrdersNotification>> getNotification(@RequestHeader String token){
        List<SellerOrdersNotification> notification = sellerNotificationService.getNotification(token);
        return new ResponseEntity<>(notification,HttpStatus.OK);
    }

    @GetMapping("/seller/orders")
    public ResponseEntity<List<SellerOrdersNotification>> getSellerOrders(@RequestHeader String token){
        List<SellerOrdersNotification> allOrdersByCustomer = sellerNotificationService.getAllOrdersByCustomer(token);
        return new ResponseEntity<>(allOrdersByCustomer,HttpStatus.OK);
    }
}
