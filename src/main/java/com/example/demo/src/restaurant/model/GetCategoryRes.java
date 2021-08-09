package com.example.demo.src.restaurant.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetCategoryRes {
    private String restaurantBigCategoryImgUrl;
    private String restaurantCategoryName;

}
