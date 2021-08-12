package com.example.demo.src.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class PostReviewReq {
    private int restaurantIdx;
    private int orderIdx;
    private int userIdx;
    private String reviewImgUrlOne;
    private String reviewImgStatus;
    private String reviewContents;
    private String reviewStar;
    private String status;
}
