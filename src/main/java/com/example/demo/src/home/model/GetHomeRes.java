package com.example.demo.src.home.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetHomeRes {
    private List<GetPromoBannerImgListRes> getPromoBannerImgListRes;
    private List<GetRestaurantCategoryListRes> getRestaurantCategoryListRes;
    private List<GetPopularFranchiseRes> getPopularFranchiseRes;
    private GetCouponBannerImgListRes getCouponBannerImgListRes;
    private List<GetNewRestaurantsListRes> getNewRestaurantsListRes;
    private List<GetFilteredRes> getFilteredRes;
}
