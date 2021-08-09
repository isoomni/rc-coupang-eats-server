package com.example.demo.src.order.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetOrderRes4 {
    private String usableCouponCount;
    private String orderAmount;
    private String deliveryFee;
    private String couponAmount;
    private String totalAmount;
    private String requestedTermToOwner;
    private String disposableItemReceivingStatus;
    private String requestedTermToDeliveryMan;
    private int paymentMethodIdx;
}
