package com.example.demo.src.restaurant;

import com.example.demo.src.home.model.*;
import com.example.demo.src.restaurant.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class RestaurantDao {

    private JdbcTemplate jdbcTemplate;
    private GetCategoryNameRes getCategoryNameRes;
    private List<GetRestaurantCategoryListRes> getRestaurantCategoryListRes;
    private List<GetNewRestaurantsListRes> getNewRestaurantsListRes;
    private List<GetFilteredRes> getFilteredRes;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public GetRestaurantRes getRestaurant(int restaurantCategoryIdx){
        // 카테고리 이름
        String getHomeQuery0 = "SELECT RC.restaurantCategoryName FROM RC_coupang_eats_d_Riley.RestaurantCategory RC where RC.restaurantCategoryIdx = ?";
        // 카테고리 리스트
        String getHomeQuery1 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";

        // 새로 들어왔어요
        String getHomeQuery2 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,COUNT(RV.reviewIdx) as reviewCount,R.distance,R.deliveryFee\n" +
                "FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx\n" +
                "where R.createdAt >= date_add(now(), interval -7 day) and R.restaurantCategoryIdx = ? GROUP BY RV.restaurantIdx";

        // 골라먹는 맛집
        String getHomeQuery3 = "SELECT R.restaurantProfileUrl, R.subProfileImgOne, R.subProfileImgTwo, R.restaurantName, R.chitaDeliveryStatus, CONCAT(R.deliveryTime,'-',(R.deliveryTime+10),'분') as deliveryTime, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg, COUNT(RV.reviewIdx) as reviewCount, R.distance, R.deliveryFee, C.couponName\n" +
                "FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx WHERE R.restaurantCategoryIdx = ? GROUP BY RV.restaurantIdx;";

        int getRestaurantParams1 = restaurantCategoryIdx;

        return new GetRestaurantRes(
                getCategoryNameRes = this.jdbcTemplate.queryForObject(getHomeQuery0,
                        (rs, rowNum)-> new GetCategoryNameRes(
                                rs.getString("restaurantCategoryName")
                        ),getRestaurantParams1),
                getRestaurantCategoryListRes = this.jdbcTemplate.query(getHomeQuery1,
                        (rs, rowNum) -> new GetRestaurantCategoryListRes(
                                rs.getString("restaurantCategoryName"),
                                rs.getString("restaurantCategoryImgUrl")
                        )),

                getNewRestaurantsListRes = this.jdbcTemplate.query(getHomeQuery2,
                        (rs, rowNum)-> new GetNewRestaurantsListRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee")
                        ),getRestaurantParams1),
                getFilteredRes = this.jdbcTemplate.query(getHomeQuery3,
                        (rs, rowNum) -> new GetFilteredRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("subProfileImgOne"),
                                rs.getString("subProfileImgTwo"),
                                rs.getString("restaurantName"),
                                rs.getString("chitaDeliveryStatus"),
                                rs.getString("deliveryTime"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponName")
                        ),getRestaurantParams1
                ));
    }
    // 치타
    public GetRestaurantRes getRestaurantByChitaFilter(int restaurantCategoryIdx, String chitaDeliveryStatus){
        // 카테고리 이름
        String getHomeQuery0 = "SELECT RC.restaurantCategoryName FROM RC_coupang_eats_d_Riley.RestaurantCategory RC where RC.restaurantCategoryIdx = ?";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,COUNT(RV.reviewIdx) as reviewCount,R.distance,R.deliveryFee FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx where R.createdAt >= date_add(now(), interval -7 day) and R.restaurantCategoryIdx = ? GROUP BY RV.restaurantIdx";

        // 골라먹는 맛집
        String getHomeQuery6 = "SELECT R.restaurantProfileUrl, R.subProfileImgOne, R.subProfileImgTwo, R.restaurantName, R.chitaDeliveryStatus, CONCAT(R.deliveryTime,'-',(R.deliveryTime+10),'분') as deliveryTime, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg, COUNT(RV.reviewIdx) as reviewCount, R.distance, R.deliveryFee, C.couponName\n" +
                "FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx WHERE R.restaurantCategoryIdx = ? and R.chitaDeliveryStatus = ? GROUP BY RV.restaurantIdx;";
        String getHomeByFilterParams1 = chitaDeliveryStatus;
        int getRestaurantParams1 = restaurantCategoryIdx;

        return new GetRestaurantRes(
                getCategoryNameRes = this.jdbcTemplate.queryForObject(getHomeQuery0,
                        (rs, rowNum)-> new GetCategoryNameRes(
                                rs.getString("restaurantCategoryName")
                        ),getRestaurantParams1),
                getRestaurantCategoryListRes = this.jdbcTemplate.query(getHomeQuery2,
                        (rs, rowNum) -> new GetRestaurantCategoryListRes(
                                rs.getString("restaurantCategoryName"),
                                rs.getString("restaurantCategoryImgUrl")
                        )),
                getNewRestaurantsListRes = this.jdbcTemplate.query(getHomeQuery5,
                        (rs, rowNum)-> new GetNewRestaurantsListRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee")
                        ),getRestaurantParams1),
                getFilteredRes = this.jdbcTemplate.query(getHomeQuery6,
                        (rs, rowNum) -> new GetFilteredRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("subProfileImgOne"),
                                rs.getString("subProfileImgTwo"),
                                rs.getString("restaurantName"),
                                rs.getString("chitaDeliveryStatus"),
                                rs.getString("deliveryTime"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponName")
                        ),getRestaurantParams1, getHomeByFilterParams1
                ));
    }

    // 쿠폰
    public GetRestaurantRes getRestaurantByCouponFilter(int restaurantCategoryIdx, String couponStatus){
        // 카테고리 이름
        String getHomeQuery0 = "SELECT RC.restaurantCategoryName FROM RC_coupang_eats_d_Riley.RestaurantCategory RC where RC.restaurantCategoryIdx = ?";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,COUNT(RV.reviewIdx) as reviewCount,R.distance,R.deliveryFee FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx where R.createdAt >= date_add(now(), interval -7 day) and R.restaurantCategoryIdx = ? GROUP BY RV.restaurantIdx";

        // 골라먹는 맛집
        String getHomeQuery6 = "SELECT R.restaurantProfileUrl, R.subProfileImgOne, R.subProfileImgTwo, R.restaurantName, R.chitaDeliveryStatus, CONCAT(R.deliveryTime,'-',(R.deliveryTime+10),'분') as deliveryTime, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg, COUNT(RV.reviewIdx) as reviewCount, R.distance, R.deliveryFee, C.couponName\n" +
                "FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx WHERE R.restaurantCategoryIdx = ? and R.couponStatus = ? GROUP BY RV.restaurantIdx;";


        String getHomeByFilterParams2 = couponStatus;
        int getRestaurantParams1 = restaurantCategoryIdx;


        return new GetRestaurantRes(
                getCategoryNameRes = this.jdbcTemplate.queryForObject(getHomeQuery0,
                        (rs, rowNum)-> new GetCategoryNameRes(
                                rs.getString("restaurantCategoryName")
                        ),getRestaurantParams1),
                getRestaurantCategoryListRes = this.jdbcTemplate.query(getHomeQuery2,
                        (rs, rowNum) -> new GetRestaurantCategoryListRes(
                                rs.getString("restaurantCategoryName"),
                                rs.getString("restaurantCategoryImgUrl")
                        )),

                getNewRestaurantsListRes = this.jdbcTemplate.query(getHomeQuery5,
                        (rs, rowNum)-> new GetNewRestaurantsListRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee")
                        )),
                getFilteredRes = this.jdbcTemplate.query(getHomeQuery6,
                        (rs, rowNum) -> new GetFilteredRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("subProfileImgOne"),
                                rs.getString("subProfileImgTwo"),
                                rs.getString("restaurantName"),
                                rs.getString("chitaDeliveryStatus"),
                                rs.getString("deliveryTime"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponName")
                        ),getHomeByFilterParams2
                ));
    }
    // 최소 주문
    public GetRestaurantRes getRestaurantByMinDeliveryAmountFilter(int restaurantCategoryIdx, Double minDeliveryAmount){
        // 카테고리 이름
        String getHomeQuery0 = "SELECT RC.restaurantCategoryName FROM RC_coupang_eats_d_Riley.RestaurantCategory RC where RC.restaurantCategoryIdx = ?";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,COUNT(RV.reviewIdx) as reviewCount,R.distance,R.deliveryFee FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx where R.createdAt >= date_add(now(), interval -7 day) and R.restaurantCategoryIdx = ? GROUP BY RV.restaurantIdx";

        // 골라먹는 맛집
        String getHomeQuery6 = "SELECT R.restaurantProfileUrl, R.subProfileImgOne, R.subProfileImgTwo, R.restaurantName, R.chitaDeliveryStatus, CONCAT(R.deliveryTime,'-',(R.deliveryTime+10),'분') as deliveryTime, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg, COUNT(RV.reviewIdx) as reviewCount, R.distance, R.deliveryFee, C.couponName\n" +
                "FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx WHERE R.restaurantCategoryIdx = ? and R.minDeliveryAmount = ? GROUP BY RV.restaurantIdx;";

        int getRestaurantParams1 = restaurantCategoryIdx;
        Double getHomeByFilterParams2 = minDeliveryAmount;


        return new GetRestaurantRes(
                getCategoryNameRes = this.jdbcTemplate.queryForObject(getHomeQuery0,
                        (rs, rowNum)-> new GetCategoryNameRes(
                                rs.getString("restaurantCategoryName")
                        ),getRestaurantParams1),
                getRestaurantCategoryListRes = this.jdbcTemplate.query(getHomeQuery2,
                        (rs, rowNum) -> new GetRestaurantCategoryListRes(
                                rs.getString("restaurantCategoryName"),
                                rs.getString("restaurantCategoryImgUrl")
                        )),

                getNewRestaurantsListRes = this.jdbcTemplate.query(getHomeQuery5,
                        (rs, rowNum)-> new GetNewRestaurantsListRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee")
                        ), getRestaurantParams1),
                getFilteredRes = this.jdbcTemplate.query(getHomeQuery6,
                        (rs, rowNum) -> new GetFilteredRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("subProfileImgOne"),
                                rs.getString("subProfileImgTwo"),
                                rs.getString("restaurantName"),
                                rs.getString("chitaDeliveryStatus"),
                                rs.getString("deliveryTime"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponName")
                        ), getRestaurantParams1,getHomeByFilterParams2
                ));
    }

    // 치타 배달 & 최소 주문
    public GetRestaurantRes getRestaurantByChitaAndMinFilter(int restaurantCategoryIdx, String chitaDeliveryStatus, Double minDeliveryAmount){
        // 카테고리 이름
        String getHomeQuery0 = "SELECT RC.restaurantCategoryName FROM RC_coupang_eats_d_Riley.RestaurantCategory RC where RC.restaurantCategoryIdx = ?";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,COUNT(RV.reviewIdx) as reviewCount,R.distance,R.deliveryFee FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx where R.createdAt >= date_add(now(), interval -7 day) and R.restaurantCategoryIdx = ? GROUP BY RV.restaurantIdx";

        // 골라먹는 맛집
        String getHomeQuery6 = "SELECT R.restaurantProfileUrl, R.subProfileImgOne, R.subProfileImgTwo, R.restaurantName, R.chitaDeliveryStatus, CONCAT(R.deliveryTime,'-',(R.deliveryTime+10),'분') as deliveryTime, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg, COUNT(RV.reviewIdx) as reviewCount, R.distance, R.deliveryFee, C.couponName\n" +
                "FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx WHERE R.restaurantCategoryIdx = ? and R.chitaDeliveryStatus = ? and R.minDeliveryAmount = ? GROUP BY RV.restaurantIdx;";

        int getRestaurantParams1 = restaurantCategoryIdx;
        String getHomeByFilterParams1 = chitaDeliveryStatus;
        Double getHomeByFilterParams2 = minDeliveryAmount;


        return new GetRestaurantRes(
                getCategoryNameRes = this.jdbcTemplate.queryForObject(getHomeQuery0,
                        (rs, rowNum)-> new GetCategoryNameRes(
                                rs.getString("restaurantCategoryName")
                        ),getRestaurantParams1),
                getRestaurantCategoryListRes = this.jdbcTemplate.query(getHomeQuery2,
                        (rs, rowNum) -> new GetRestaurantCategoryListRes(
                                rs.getString("restaurantCategoryName"),
                                rs.getString("restaurantCategoryImgUrl")
                        )),

                getNewRestaurantsListRes = this.jdbcTemplate.query(getHomeQuery5,
                        (rs, rowNum)-> new GetNewRestaurantsListRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee")
                        ), getRestaurantParams1),
                getFilteredRes = this.jdbcTemplate.query(getHomeQuery6,
                        (rs, rowNum) -> new GetFilteredRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("subProfileImgOne"),
                                rs.getString("subProfileImgTwo"),
                                rs.getString("restaurantName"),
                                rs.getString("chitaDeliveryStatus"),
                                rs.getString("deliveryTime"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponName")
                        ), getRestaurantParams1,getHomeByFilterParams1, getHomeByFilterParams2
                ));
    }

    // 치타 배달 & 쿠폰
    public GetRestaurantRes getRestaurantByChitaAndCouponFilter(int restaurantCategoryIdx, String chitaDeliveryStatus, String couponStatus){

        // 카테고리 이름
        String getHomeQuery0 = "SELECT RC.restaurantCategoryName FROM RC_coupang_eats_d_Riley.RestaurantCategory RC where RC.restaurantCategoryIdx = ?";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,COUNT(RV.reviewIdx) as reviewCount,R.distance,R.deliveryFee FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx where R.createdAt >= date_add(now(), interval -7 day) and R.restaurantCategoryIdx = ? GROUP BY RV.restaurantIdx";

        // 골라먹는 맛집
        String getHomeQuery6 = "SELECT R.restaurantProfileUrl, R.subProfileImgOne, R.subProfileImgTwo, R.restaurantName, R.chitaDeliveryStatus, CONCAT(R.deliveryTime,'-',(R.deliveryTime+10),'분') as deliveryTime, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg, COUNT(RV.reviewIdx) as reviewCount, R.distance, R.deliveryFee, C.couponName\n" +
                "FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx WHERE R.chitaDeliveryStatus = ? and R.couponStatus = ? GROUP BY RV.restaurantIdx;";

        int getRestaurantParams1 = restaurantCategoryIdx;
        String getHomeByFilterParams1 = chitaDeliveryStatus;
        String getHomeByFilterParams2 = couponStatus;


        return new GetRestaurantRes(
                getCategoryNameRes = this.jdbcTemplate.queryForObject(getHomeQuery0,
                        (rs, rowNum)-> new GetCategoryNameRes(
                                rs.getString("restaurantCategoryName")
                        ),getRestaurantParams1),
                getRestaurantCategoryListRes = this.jdbcTemplate.query(getHomeQuery2,
                        (rs, rowNum) -> new GetRestaurantCategoryListRes(
                                rs.getString("restaurantCategoryName"),
                                rs.getString("restaurantCategoryImgUrl")
                        )),

                getNewRestaurantsListRes = this.jdbcTemplate.query(getHomeQuery5,
                        (rs, rowNum)-> new GetNewRestaurantsListRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee")
                        ), getRestaurantParams1),
                getFilteredRes = this.jdbcTemplate.query(getHomeQuery6,
                        (rs, rowNum) -> new GetFilteredRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("subProfileImgOne"),
                                rs.getString("subProfileImgTwo"),
                                rs.getString("restaurantName"),
                                rs.getString("chitaDeliveryStatus"),
                                rs.getString("deliveryTime"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponName")
                        ), getRestaurantParams1,getHomeByFilterParams1, getHomeByFilterParams2
                ));
    }
    // 쿠폰 & 최소 주문
    public GetRestaurantRes getRestaurantByCouponAndMinFilter(int restaurantCategoryIdx, Double minDeliveryAmount, String couponStatus){
        // 카테고리 이름
        String getHomeQuery0 = "SELECT RC.restaurantCategoryName FROM RC_coupang_eats_d_Riley.RestaurantCategory RC where RC.restaurantCategoryIdx = ?";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,COUNT(RV.reviewIdx) as reviewCount,R.distance,R.deliveryFee FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx where R.createdAt >= date_add(now(), interval -7 day) and R.restaurantCategoryIdx = ? GROUP BY RV.restaurantIdx";

        // 골라먹는 맛집
        String getHomeQuery6 = "SELECT R.restaurantProfileUrl, R.subProfileImgOne, R.subProfileImgTwo, R.restaurantName, R.chitaDeliveryStatus, CONCAT(R.deliveryTime,'-',(R.deliveryTime+10),'분') as deliveryTime, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg, COUNT(RV.reviewIdx) as reviewCount, R.distance, R.deliveryFee, C.couponName\n" +
                "FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx WHERE R.restaurantCategoryIdx = ? and R.minDeliveryAmount = ? and R.couponStatus = ? GROUP BY RV.restaurantIdx;";

        int getRestaurantParams1 = restaurantCategoryIdx;
        Double getHomeByFilterParams1 = minDeliveryAmount;
        String getHomeByFilterParams2 = couponStatus;


        return new GetRestaurantRes(
                getCategoryNameRes = this.jdbcTemplate.queryForObject(getHomeQuery0,
                        (rs, rowNum)-> new GetCategoryNameRes(
                                rs.getString("restaurantCategoryName")
                        ),getRestaurantParams1),
                getRestaurantCategoryListRes = this.jdbcTemplate.query(getHomeQuery2,
                        (rs, rowNum) -> new GetRestaurantCategoryListRes(
                                rs.getString("restaurantCategoryName"),
                                rs.getString("restaurantCategoryImgUrl")
                        )),

                getNewRestaurantsListRes = this.jdbcTemplate.query(getHomeQuery5,
                        (rs, rowNum)-> new GetNewRestaurantsListRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee")
                        ), getRestaurantParams1),
                getFilteredRes = this.jdbcTemplate.query(getHomeQuery6,
                        (rs, rowNum) -> new GetFilteredRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("subProfileImgOne"),
                                rs.getString("subProfileImgTwo"),
                                rs.getString("restaurantName"),
                                rs.getString("chitaDeliveryStatus"),
                                rs.getString("deliveryTime"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponName")
                        ), getRestaurantParams1,getHomeByFilterParams1, getHomeByFilterParams2
                ));
    }
    public GetRestaurantRes getRestaurantByFilter(int restaurantCategoryIdx, String chitaDeliveryStatus, String couponStatus, Double minDeliveryAmount){

        // 카테고리 이름
        String getHomeQuery0 = "SELECT RC.restaurantCategoryName FROM RC_coupang_eats_d_Riley.RestaurantCategory RC where RC.restaurantCategoryIdx = ?";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,COUNT(RV.reviewIdx) as reviewCount,R.distance,R.deliveryFee FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx where R.createdAt >= date_add(now(), interval -7 day) and R.restaurantCategoryIdx = ? GROUP BY RV.restaurantIdx";

        // 골라먹는 맛집
        String getHomeQuery6 = "SELECT R.restaurantProfileUrl, R.subProfileImgOne, R.subProfileImgTwo, R.restaurantName, R.chitaDeliveryStatus, CONCAT(R.deliveryTime,'-',(R.deliveryTime+10),'분') as deliveryTime, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg, COUNT(RV.reviewIdx) as reviewCount, R.distance, R.deliveryFee, C.couponName\n" +
                "FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx WHERE R.restaurantCategoryIdx = ? and R.chitaDeliveryStatus = ? and R.couponStatus = ? and R.minDeliveryAmount = ? GROUP BY RV.restaurantIdx;";

        int getRestaurantParams1 = restaurantCategoryIdx;
        String getHomeByFilterParams1 = chitaDeliveryStatus;
        String getHomeByFilterParams2 = couponStatus;
        Double getHomeByFilterParams3 = minDeliveryAmount;

        return new GetRestaurantRes(
                getCategoryNameRes = this.jdbcTemplate.queryForObject(getHomeQuery0,
                        (rs, rowNum)-> new GetCategoryNameRes(
                                rs.getString("restaurantCategoryName")
                        ),getRestaurantParams1),
                getRestaurantCategoryListRes = this.jdbcTemplate.query(getHomeQuery2,
                        (rs, rowNum) -> new GetRestaurantCategoryListRes(
                                rs.getString("restaurantCategoryName"),
                                rs.getString("restaurantCategoryImgUrl")
                        )),

                getNewRestaurantsListRes = this.jdbcTemplate.query(getHomeQuery5,
                        (rs, rowNum)-> new GetNewRestaurantsListRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee")
                        ), getRestaurantParams1),
                getFilteredRes = this.jdbcTemplate.query(getHomeQuery6,
                        (rs, rowNum) -> new GetFilteredRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("subProfileImgOne"),
                                rs.getString("subProfileImgTwo"),
                                rs.getString("restaurantName"),
                                rs.getString("chitaDeliveryStatus"),
                                rs.getString("deliveryTime"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getDouble("distance"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponName")
                        ), getRestaurantParams1,getHomeByFilterParams1, getHomeByFilterParams2, getHomeByFilterParams3
                ));
    }



}
