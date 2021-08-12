package com.example.demo.src.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class PostOrderReq {
    private int restaurantIdx;
    private int userIdx;
    private int paymentMethodIdx;
    private String requestedTermToOwner;
    private String requestedTermToDeliveryMan;
    private String disposableItemReceivingStatus;
    private int cartIdx;
    private int menuIdx;
    private int menuQuantity;
}
