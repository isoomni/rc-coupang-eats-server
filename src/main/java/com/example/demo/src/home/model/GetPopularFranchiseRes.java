package com.example.demo.src.home.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPopularFranchiseRes {
    private String restaurantProfileUrl;
    private String restaurantName;
    private Double starAvg;
    private String reviewCount;
    private Double distance;
    private String deliveryFee;
}
