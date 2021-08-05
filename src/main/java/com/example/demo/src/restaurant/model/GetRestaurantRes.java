package com.example.demo.src.restaurant.model;
import com.example.demo.src.home.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetRestaurantRes {
    private GetCategoryNameRes getCategoryNameRes;
    private List<GetRestaurantCategoryListRes> getRestaurantCategoryListRes;
    private List<GetNewRestaurantsListRes> getNewRestaurantsListRes;
    private List<GetFilteredRes> getFilteredRes;
}
