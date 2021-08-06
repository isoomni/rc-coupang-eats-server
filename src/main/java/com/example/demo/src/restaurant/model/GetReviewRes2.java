package com.example.demo.src.restaurant.model;
import com.example.demo.src.home.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetReviewRes2 {
    private String userName;
    private Double reviewStar;
    private String reviewCreatedAt;
    private String reviewImgUrlOne;
    private String reviewImgUrlTwo;
    private String reviewContents;
    private String menuName;
    private String managerReviewCreatedAt;
    private String callUserName;
    private String managerReviewContents;  // 안되면 orderIdx 추가하기
}
