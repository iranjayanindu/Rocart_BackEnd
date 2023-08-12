package com.masai.controller;

import com.masai.models.SellerOrdersNotification;
import com.masai.service.SellerNotificationService;
import org.apache.catalina.LifecycleState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/notification/read")
    public ResponseEntity<SellerOrdersNotification> updateNotificationStatus(@RequestParam int id){
        SellerOrdersNotification sellerOrdersNotification = sellerNotificationService.updateOrderNotification(id);
        return new ResponseEntity<>(sellerOrdersNotification,HttpStatus.CREATED);
    }

    @PutMapping("/update/order")
    public ResponseEntity<SellerOrdersNotification> updaterOderStatus(@RequestParam int notiId ,@RequestParam int type){
        SellerOrdersNotification sellerOrdersNotification = sellerNotificationService.updateOrderDilevery(notiId,type);
        return new ResponseEntity<>(sellerOrdersNotification,HttpStatus.CREATED);
    }

    @GetMapping("/seller/orders")
    public ResponseEntity<List<SellerOrdersNotification>> getSellerOrders(@RequestHeader String token){
        List<SellerOrdersNotification> allOrdersByCustomer = sellerNotificationService.getAllOrdersByCustomer(token);
        return new ResponseEntity<>(allOrdersByCustomer,HttpStatus.OK);
    }

    @GetMapping("/seller/order/status/count")
    public ResponseEntity<Integer> getSellerOrdersStatusCount(@RequestHeader String token,@RequestParam int status)throws Exception{
        int i = sellerNotificationService.sellerOrderStatuesCount(token, status);
        return new ResponseEntity<>(i,HttpStatus.OK);
    }

    @GetMapping("/seller/orders/by/status")
    public ResponseEntity<List<SellerOrdersNotification>> getSellerOrderByStatusList(@RequestHeader String token,@RequestParam int status){
        List<SellerOrdersNotification> allOrdersByStatus = sellerNotificationService.getAllOrdersByStatus(token, status);
        return new ResponseEntity<>(allOrdersByStatus,HttpStatus.OK);
    }
}
