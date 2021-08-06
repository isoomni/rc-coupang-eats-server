package com.example.demo.src.restaurant.model;
import com.example.demo.src.home.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetRestaurantMenuRes {
    private GetRestaurantNameAndLikeRes getRestaurantNameAndLikeRes;
    private List<GetMenuCategoryListRes> getMenuCategoryListRes;
    private List<GetMenuListRes> getMenuListRes;
}
