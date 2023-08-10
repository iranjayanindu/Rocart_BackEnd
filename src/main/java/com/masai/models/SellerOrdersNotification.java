package com.masai.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class SellerOrdersNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer notificationId;


    private Date dateCreate;

    private Integer productid;

    private int qty;

    private Integer customerId;

    private Integer sellerId;

    @Enumerated(EnumType.STRING)
    private OrderStatusValues orderStatus;

    private String addressType;

    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;

}
