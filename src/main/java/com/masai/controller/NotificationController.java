package com.masai.controller;

import com.masai.models.SellerOrdersNotification;
import com.masai.service.SellerNotificationService;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NotificationController {

    @Autowired
    private SellerNotificationService sellerNotificationService;

    @GetMapping("/notification/count")
    public ResponseEntity<Integer> getNotificationCount() throws Exception{
        Integer i = sellerNotificationService.notificationCount();
        return new ResponseEntity<>(i, HttpStatus.OK);
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<SellerOrdersNotification>> getNotification(){
        List<SellerOrdersNotification> notification = sellerNotificationService.getNotification();
        return new ResponseEntity<>(notification,HttpStatus.OK);
    }
}
