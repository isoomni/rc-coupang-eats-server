package com.example.demo.src.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class PatchReviewReq {
    private int reviewIdx;
    private String reviewImgUrlOne;
    private String reviewImgStatus;
    private String reviewContents;
    private Double reviewStar;

}
