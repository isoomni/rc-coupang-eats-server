package com.example.demo.src.home;

import com.example.demo.src.home.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class HomeDao {

    private JdbcTemplate jdbcTemplate;
    private List<GetRestaurantCategoryListRes> getRestaurantCategoryListRes;
    private List<GetPopularFranchiseRes> getPopularFranchiseRes;
    private List<GetFilteredRes> getFilteredRes;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public GetHomeRes getHome(){
        // 카테고리 리스트
        String getHomeQuery1 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";
        // 인기 프랜차이즈
        String getHomeQuery2 = "SELECT R.restaurantProfileUrl, R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg, COUNT(RV.reviewIdx) as reviewCount, R.distance, R.deliveryFee\n" +
                "FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx GROUP BY RV.restaurantIdx;";
        // 골라먹는 맛집
        String getHomeQuery3 = "SELECT R.restaurantProfileUrl, R.restaurantName, R.chitaDeliveryStatus, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg, COUNT(RV.reviewIdx) as reviewCount, R.distance, R.deliveryFee, C.couponName\n" +
                "FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx GROUP BY RV.restaurantIdx;";

        return new GetHomeRes(
                getRestaurantCategoryListRes = this.jdbcTemplate.query(getHomeQuery1,
                        (rs, rowNum) -> new GetRestaurantCategoryListRes(
                                rs.getString("restaurantCategoryName"),
                                rs.getString("restaurantCategoryImgUrl")
                        )),
                getPopularFranchiseRes = this.jdbcTemplate.query(getHomeQuery2,
                        (rs, rowNum)-> new GetPopularFranchiseRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee")

                        )),
                getFilteredRes = this.jdbcTemplate.query(getHomeQuery3,
                        (rs, rowNum) -> new GetFilteredRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getString("chitaDeliveryStatus"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponName")
                        )
                ));
    }






}
