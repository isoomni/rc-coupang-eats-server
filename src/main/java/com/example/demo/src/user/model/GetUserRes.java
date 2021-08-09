package com.example.demo.src.user.model;


import com.example.demo.src.home.model.GetCouponBannerImgListRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserRes {
    private GetUserRes1 getUserRes1;
    private GetCouponBannerImgListRes getCouponBannerImgListRes;
}
