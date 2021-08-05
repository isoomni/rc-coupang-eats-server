package com.example.demo.src.home.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetFilteredRes {
    private String restaurantProfileUrl;
    private String subProfileImgOne;
    private String subProfileImgTwo;
    private String restaurantName;
    private String chitaDeliveryStatus;
    private String deliveryTime;
    private Double starAvg;
    private String reviewCount;
    private Double distance;
    private String deliveryFee;
    private String couponName;
}
