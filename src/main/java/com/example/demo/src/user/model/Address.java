package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Address {
    private int deliveryAddressIdx;
    private String addressTitle;
    private String roadNameAddress;
    private String detailedAddress;
    private Double userLatitude;
    private Double userLongtitude;
}
