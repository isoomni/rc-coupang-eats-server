package com.example.demo.src.restaurant.model;
import com.example.demo.src.home.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetMenuListRes {
    private String menuCategoryName;
    private String bestOrder;
    private String bestReview;
    private String menuName;
    private Double menuAmount;
    private String menuContents;
    private String menuImgUrl;
}
