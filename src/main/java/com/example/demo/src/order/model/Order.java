package com.example.demo.src.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class Order {
    private String requestedTermToOwner;
    private String requestedTermToDeliveryMan;
    private String disposableItemReceivingStatus;
    private int userIdx;
    private int cartIdx;
    private String status;
}
