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

    private GetRestaurantNameAndLikeRes getRestaurantNameAndLikeRes;
    private List<GetMenuCategoryListRes> getMenuCategoryListRes;
    private List<GetMenuListRes> getMenuListRes;

    private GetReviewRes1 getReviewRes1;
    private List<GetReviewRes2> getReviewRes2;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    /**
     * 카테고리 별 식당 조회 API
     * [GET] /restaurants/:restaurantCategoryIdx
     * @return BaseResponse<GetRestaurantRes>
     */
    public GetRestaurantRes getRestaurant(int restaurantCategoryIdx, int deliveryAddressIdx){
        // 카테고리 이름
        String getHomeQuery0 = "SELECT RC.restaurantCategoryName FROM RC_coupang_eats_d_Riley.RestaurantCategory RC where RC.restaurantCategoryIdx = ?";
        // 카테고리 리스트
        String getHomeQuery1 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";

        // 새로 들어왔어요
        String getHomeQuery2 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount,\n" +
                "       concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,\n" +
                "       if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                where R.createdAt >= date_add(now(), interval -7 day) and R.restaurantCategoryIdx = ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY realDistance;";

        // 골라먹는 맛집
        String getHomeQuery3 = "SELECT R.restaurantProfileUrl, R.subProfileImgOne, R.subProfileImgTwo, R.restaurantName, R.chitaDeliveryStatus,\n" +
                "                CONCAT(R.deliveryTime,'-',(R.deliveryTime+10),'분') as deliveryTime, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount, concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,  if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee, C.couponName\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx\n" +
                "                LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "where R.restaurantCategoryIdx = ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY starAvg;";

        int getRestaurantParams1 = restaurantCategoryIdx;
        int getHomeByFilterParams4 = deliveryAddressIdx;

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
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")
                        ), getRestaurantParams1, getHomeByFilterParams4),
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
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponName")
                        ), getRestaurantParams1, getHomeByFilterParams4
                ));
    }
    // 치타
    public GetRestaurantRes getRestaurantByChitaFilter(int restaurantCategoryIdx, int deliveryAddressIdx, String chitaDeliveryStatus){
        // 카테고리 이름
        String getHomeQuery0 = "SELECT RC.restaurantCategoryName FROM RC_coupang_eats_d_Riley.RestaurantCategory RC where RC.restaurantCategoryIdx = ?";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount,\n" +
                "       concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,\n" +
                "       if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                where R.createdAt >= date_add(now(), interval -7 day) and R.restaurantCategoryIdx = ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY realDistance;";

        // 골라먹는 맛집
        String getHomeQuery6 = "SELECT R.restaurantProfileUrl, R.subProfileImgOne, R.subProfileImgTwo, R.restaurantName, R.chitaDeliveryStatus,\n" +
                "                CONCAT(R.deliveryTime,'-',(R.deliveryTime+10),'분') as deliveryTime, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount, concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,  if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee, C.couponName\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx\n" +
                "                LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "where R.restaurantCategoryIdx = ? and R.chitaDeliveryStatus = ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY starAvg;";

        String getHomeByFilterParams1 = chitaDeliveryStatus;
        int getRestaurantParams1 = restaurantCategoryIdx;
        int getHomeByFilterParams4 = deliveryAddressIdx;

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
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")
                        ),getRestaurantParams1, getHomeByFilterParams4),

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
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponName")
                        ),getRestaurantParams1, getHomeByFilterParams1, getHomeByFilterParams4
                ));
    }

    // 쿠폰
    public GetRestaurantRes getRestaurantByCouponFilter(int restaurantCategoryIdx, int deliveryAddressIdx, String couponStatus){
        // 카테고리 이름
        String getHomeQuery0 = "SELECT RC.restaurantCategoryName FROM RC_coupang_eats_d_Riley.RestaurantCategory RC where RC.restaurantCategoryIdx = ?";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount,\n" +
                "       concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,\n" +
                "       if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                where R.createdAt >= date_add(now(), interval -7 day) and R.restaurantCategoryIdx = ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY realDistance;";

        // 골라먹는 맛집
        String getHomeQuery6 = "SELECT R.restaurantProfileUrl, R.subProfileImgOne, R.subProfileImgTwo, R.restaurantName, R.chitaDeliveryStatus,\n" +
                "                CONCAT(R.deliveryTime,'-',(R.deliveryTime+10),'분') as deliveryTime, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount, concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,  if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee, C.couponName\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx\n" +
                "                LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "where R.restaurantCategoryIdx = ? and R.couponStatus = ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY starAvg;";

        int getRestaurantParams1 = restaurantCategoryIdx;
        String getHomeByFilterParams2 = couponStatus;
        int getHomeByFilterParams4 = deliveryAddressIdx;



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
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")
                        ), getRestaurantParams1, getHomeByFilterParams4),
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
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponName")
                        ), getRestaurantParams1,getHomeByFilterParams2, getHomeByFilterParams4
                ));
    }
    // 최소 주문
    public GetRestaurantRes getRestaurantByMinDeliveryAmountFilter(int restaurantCategoryIdx, int deliveryAddressIdx, Double minDeliveryAmount){
        // 카테고리 이름
        String getHomeQuery0 = "SELECT RC.restaurantCategoryName FROM RC_coupang_eats_d_Riley.RestaurantCategory RC where RC.restaurantCategoryIdx = ?";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount,\n" +
                "       concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,\n" +
                "       if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                where R.createdAt >= date_add(now(), interval -7 day) and R.restaurantCategoryIdx = ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY realDistance;";

        // 골라먹는 맛집
        String getHomeQuery6 = "SELECT R.restaurantProfileUrl, R.subProfileImgOne, R.subProfileImgTwo, R.restaurantName, R.chitaDeliveryStatus,\n" +
                "                CONCAT(R.deliveryTime,'-',(R.deliveryTime+10),'분') as deliveryTime, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount, concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,  if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee, C.couponName\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx\n" +
                "                LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "where R.restaurantCategoryIdx = ? and R.minDeliveryAmount <= ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY starAvg;";

        int getRestaurantParams1 = restaurantCategoryIdx;
        Double getHomeByFilterParams2 = minDeliveryAmount;
        int getHomeByFilterParams4 = deliveryAddressIdx;


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
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")
                        ), getRestaurantParams1, getHomeByFilterParams4),
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
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponName")
                        ), getRestaurantParams1, getHomeByFilterParams2, getHomeByFilterParams4
                ));
    }

    // 치타 배달 & 최소 주문
    public GetRestaurantRes getRestaurantByChitaAndMinFilter(int restaurantCategoryIdx, int deliveryAddressIdx, String chitaDeliveryStatus, Double minDeliveryAmount){
        // 카테고리 이름
        String getHomeQuery0 = "SELECT RC.restaurantCategoryName FROM RC_coupang_eats_d_Riley.RestaurantCategory RC where RC.restaurantCategoryIdx = ?";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount,\n" +
                "       concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,\n" +
                "       if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                where R.createdAt >= date_add(now(), interval -7 day) and R.restaurantCategoryIdx = ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY realDistance;";

        // 골라먹는 맛집
        String getHomeQuery6 = "SELECT R.restaurantProfileUrl, R.subProfileImgOne, R.subProfileImgTwo, R.restaurantName, R.chitaDeliveryStatus,\n" +
                "                CONCAT(R.deliveryTime,'-',(R.deliveryTime+10),'분') as deliveryTime, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount, concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,  if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee, C.couponName\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx\n" +
                "                LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "where R.restaurantCategoryIdx = ? and R.chitaDeliveryStatus = ? and R.minDeliveryAmount <= ?  and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY starAvg;";

        int getRestaurantParams1 = restaurantCategoryIdx;
        String getHomeByFilterParams1 = chitaDeliveryStatus;
        Double getHomeByFilterParams2 = minDeliveryAmount;
        int getHomeByFilterParams4 = deliveryAddressIdx;


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
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")
                        ), getRestaurantParams1, getHomeByFilterParams4),
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
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponName")
                        ), getRestaurantParams1,getHomeByFilterParams1, getHomeByFilterParams2, getHomeByFilterParams4
                ));
    }

    // 치타 배달 & 쿠폰
    public GetRestaurantRes getRestaurantByChitaAndCouponFilter(int restaurantCategoryIdx, int deliveryAddressIdx, String chitaDeliveryStatus, String couponStatus){

        // 카테고리 이름
        String getHomeQuery0 = "SELECT RC.restaurantCategoryName FROM RC_coupang_eats_d_Riley.RestaurantCategory RC where RC.restaurantCategoryIdx = ?";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount,\n" +
                "       concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,\n" +
                "       if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                where R.createdAt >= date_add(now(), interval -7 day) and R.restaurantCategoryIdx = ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY realDistance;";

        // 골라먹는 맛집
        String getHomeQuery6 = "SELECT R.restaurantProfileUrl, R.subProfileImgOne, R.subProfileImgTwo, R.restaurantName, R.chitaDeliveryStatus,\n" +
                "                CONCAT(R.deliveryTime,'-',(R.deliveryTime+10),'분') as deliveryTime, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount, concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,  if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee, C.couponName\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx\n" +
                "                LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "where R.restaurantCategoryIdx = ? and R.chitaDeliveryStatus = ? and R.couponStatus = ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY starAvg;";

        int getRestaurantParams1 = restaurantCategoryIdx;
        String getHomeByFilterParams1 = chitaDeliveryStatus;
        String getHomeByFilterParams2 = couponStatus;
        int getHomeByFilterParams4 = deliveryAddressIdx;


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
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")
                        ), getRestaurantParams1, getHomeByFilterParams4),
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
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponName")
                        ), getRestaurantParams1,getHomeByFilterParams1, getHomeByFilterParams2, getHomeByFilterParams4
                ));
    }
    // 쿠폰 & 최소 주문
    public GetRestaurantRes getRestaurantByCouponAndMinFilter(int restaurantCategoryIdx, int deliveryAddressIdx, Double minDeliveryAmount, String couponStatus){
        // 카테고리 이름
        String getHomeQuery0 = "SELECT RC.restaurantCategoryName FROM RC_coupang_eats_d_Riley.RestaurantCategory RC where RC.restaurantCategoryIdx = ?";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount,\n" +
                "       concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,\n" +
                "       if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                where R.createdAt >= date_add(now(), interval -7 day) and R.restaurantCategoryIdx = ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY realDistance;";

        // 골라먹는 맛집
        String getHomeQuery6 = "SELECT R.restaurantProfileUrl, R.subProfileImgOne, R.subProfileImgTwo, R.restaurantName, R.chitaDeliveryStatus,\n" +
                "                CONCAT(R.deliveryTime,'-',(R.deliveryTime+10),'분') as deliveryTime, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount, concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,  if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee, C.couponName\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx\n" +
                "                LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "where R.restaurantCategoryIdx = ? and R.minDeliveryAmount <= ? and R.couponStatus = ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY starAvg;";

        int getRestaurantParams1 = restaurantCategoryIdx;
        Double getHomeByFilterParams1 = minDeliveryAmount;
        String getHomeByFilterParams2 = couponStatus;
        int getHomeByFilterParams4 = deliveryAddressIdx;


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
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")
                        ), getRestaurantParams1, getHomeByFilterParams4),
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
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponName")
                        ), getRestaurantParams1,getHomeByFilterParams1, getHomeByFilterParams2, getHomeByFilterParams4
                ));
    }
    public GetRestaurantRes getRestaurantByFilter(int restaurantCategoryIdx, int deliveryAddressIdx, String chitaDeliveryStatus, String couponStatus, Double minDeliveryAmount){

        // 카테고리 이름
        String getHomeQuery0 = "SELECT RC.restaurantCategoryName FROM RC_coupang_eats_d_Riley.RestaurantCategory RC where RC.restaurantCategoryIdx = ?";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount,\n" +
                "       concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,\n" +
                "       if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                where R.createdAt >= date_add(now(), interval -7 day) and R.restaurantCategoryIdx = ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY realDistance;";

        // 골라먹는 맛집
        String getHomeQuery6 = "SELECT R.restaurantProfileUrl, R.subProfileImgOne, R.subProfileImgTwo, R.restaurantName, R.chitaDeliveryStatus,\n" +
                "                CONCAT(R.deliveryTime,'-',(R.deliveryTime+10),'분') as deliveryTime, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount, concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,  if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee, C.couponName\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx\n" +
                "                LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "where R.restaurantCategoryIdx = ? and R.chitaDeliveryStatus = ? and R.couponStatus = ? and R.minDeliveryAmount <= ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY starAvg;";

        int getRestaurantParams1 = restaurantCategoryIdx;
        String getHomeByFilterParams1 = chitaDeliveryStatus;
        String getHomeByFilterParams2 = couponStatus;
        Double getHomeByFilterParams3 = minDeliveryAmount;
        int getHomeByFilterParams4 = deliveryAddressIdx;

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
                                rs.getString("distance"),
                                rs.getString("deliveryFee")
                        ), getRestaurantParams1, getHomeByFilterParams4),
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
                                rs.getString("distance"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponName")
                        ), getRestaurantParams1,getHomeByFilterParams1, getHomeByFilterParams2, getHomeByFilterParams3, getHomeByFilterParams4
                ));
    }

    /**
     * 식당 메뉴 조회 API
     * [GET] /restaurants/:restaurantIdx
     * @return BaseResponse<GetRestaurantRes>
     */

    public GetRestaurantMenuRes getRestaurantMenu(int userIdx, int restaurantIdx){
        // 식당 이름, 식당 좋아요 status
        String Query1 = "SELECT R.restaurantName, if(F.userIdx = ?, 'Y', 'N') favoriteStatus\n" +
                "FROM RC_coupang_eats_d_Riley.Favorites F LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R on F.restaurantIdx = R.restaurantIdx\n" +
                "where F.userIdx = ? and F.restaurantIdx = ?;";

        // 카테고리명 List
        String Query2 = "SELECT MC.menuCategoryName FROM RC_coupang_eats_d_Riley.MenuCategory MC\n" +
                "where MC.restaurantIdx = ?;";

        // 메뉴 리스트
        String Query3 = "SELECT MC.menuCategoryName, if(M.bestOrder='Y', '주문많음', null) as bestOrder, if(M.bestReview='Y', '리뷰최고', null) as bestReview, M.menuName, M.menuAmount, M.menuContents, M.menuImgUrl FROM RC_coupang_eats_d_Riley.Menu M LEFT JOIN RC_coupang_eats_d_Riley.MenuCategory MC on M.menuCategoryIdx = MC.menuCategoryIdx WHERE M.restaurantIdx = ?;";

        int getRestaurantParams1 = userIdx;
        int getRestaurantParams2 = restaurantIdx;

        return new GetRestaurantMenuRes(
                getRestaurantNameAndLikeRes = this.jdbcTemplate.queryForObject(Query1,
                        (rs, rowNum)-> new GetRestaurantNameAndLikeRes(
                                rs.getString("restaurantName"),
                                rs.getString("favoriteStatus")
                        ), getRestaurantParams1, getRestaurantParams1, getRestaurantParams2
                ),
                getMenuCategoryListRes = this.jdbcTemplate.query(Query2,
                        (rs, rowNum)-> new GetMenuCategoryListRes(
                                rs.getString("menuCategoryName")
                        ), getRestaurantParams2
                ),
                getMenuListRes = this.jdbcTemplate.query(Query3,
                        (rs, rowNum)-> new GetMenuListRes(
                                rs.getString("menuCategoryName"),
                                rs.getString("bestOrder"),
                                rs.getString("bestReview"),
                                rs.getString("menuName"),
                                rs.getDouble("menuAmount"),
                                rs.getString("menuContents"),
                                rs.getString("menuImgUrl")
                        ), getRestaurantParams2
                ));
    }
    /**
     * 식당 리뷰 조회 API
     * [GET] /restaurants/:restaurantIdx/review
     * @return BaseResponse<GetReviewRes>
     */
    public GetReviewsRes getReviews(int restaurantIdx){
        String Query1 = "SELECT R.restaurantName, round(SUM(Rv.reviewStar)/COUNT(Rv.reviewIdx),1) as starAvg, concat('리뷰 ', COUNT(Rv.reviewIdx), '개') as reviewCount \n" +
                "FROM RC_coupang_eats_d_Riley.Review Rv \n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON Rv.restaurantIdx = R.restaurantIdx \n" +
                "where R.restaurantIdx = ?\n" +
                "GROUP BY Rv.restaurantIdx;";
        String Query2 = "SELECT replace(U.userName, SUBSTRING(U.userName,2,2), '**') as userName, Rv.reviewStar, case\n" +
                "when date(Rv.createdAt) = curdate() then '오늘' when date(Rv.createdAt) = curdate()-1 then '1일 전'\n" +
                "when date(Rv.createdAt) = curdate()-2 then '2일 전' when date(Rv.createdAt) = curdate()-3 then '3일 전'\n" +
                "when date(Rv.createdAt) = curdate()-4 then '4일 전' when date(Rv.createdAt) = curdate()-5 then '5일 전'\n" +
                "when date(Rv.createdAt) = curdate()-6 then '6일 전'\n" +
                "when date(Rv.createdAt) <= curdate()-7 and date(Rv.createdAt) > curdate()-14 then '지난 주'\n" +
                "when date(Rv.createdAt) <= curdate()-14 and date(Rv.createdAt) > curdate()-21 then '2주 전'\n" +
                "when date(Rv.createdAt) <= curdate()-21 and date(Rv.createdAt) > curdate()-28 then '3주 전'\n" +
                "when date(Rv.createdAt) <= curdate()-7 and date(Rv.createdAt) > curdate()-14 then '1달 전'\n" +
                "else '1달 전' end as reviewCreatedAt\n" +
                ", Rv.reviewImgUrlOne, Rv.reviewImgUrlTwo, Rv.reviewContents, (SELECT GROUP_CONCAT(M.menuName separator '·')) as menuName,\n" +
                "case\n" +
                "when date(MR.createdAt) = curdate() then '오늘'\n" +
                "when date(MR.createdAt) = curdate()-1 then '1일 전'\n" +
                "when date(MR.createdAt) = curdate()-2 then '2일 전'\n" +
                "when date(MR.createdAt) = curdate()-3 then '3일 전'\n" +
                "when date(MR.createdAt) = curdate()-4 then '4일 전'\n" +
                "when date(MR.createdAt) = curdate()-5 then '5일 전'\n" +
                "when date(MR.createdAt) = curdate()-6 then '6일 전'\n" +
                "when date(MR.createdAt) <= curdate()-7 and date(MR.createdAt) > curdate()-14 then '지난 주'\n" +
                "when date(MR.createdAt) <= curdate()-14 and date(MR.createdAt) > curdate()-21 then '2주 전'\n" +
                "when date(MR.createdAt) <= curdate()-21 and date(MR.createdAt) > curdate()-28 then '3주 전'\n" +
                "when date(MR.createdAt) <= curdate()-7 and date(MR.createdAt) > curdate()-14 then '1달 전'\n" +
                "else '1달 전' end as managerReviewCreatedAt,\n" +
                "replace(U.userName, SUBSTRING(U.userName,2,2), '**님,') as callUserName, MR.managerReviewContents\n" +
                "FROM RC_coupang_eats_d_Riley.Review Rv\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.User U on Rv.userIdx = U.userIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.ManagerReview MR on Rv.reviewIdx = MR.reviewIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.Order O on Rv.orderIdx = O.orderIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.Cart C on O.cartIdx = C.cartIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.CartedMenu CM on C.cartIdx = CM.cartIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.Menu M on CM.menuIdx = M.menuIdx\n" +
                "where Rv.restaurantIdx = ?\n" +
                "group by O.orderIdx;";

        int getReviewsParams1 = restaurantIdx;

        return new GetReviewsRes(
            getReviewRes1 = this.jdbcTemplate.queryForObject(Query1,
                    (rs, rowNum)-> new GetReviewRes1(
                            rs.getString("restaurantName"),
                            rs.getDouble("starAvg"),
                            rs.getString("reviewCount")
                    ), getReviewsParams1
            ),
            getReviewRes2 = this.jdbcTemplate.query(Query2,
                    (rs, rowNum)-> new GetReviewRes2(
                            rs.getString("userName"),
                            rs.getDouble("reviewStar"),
                            rs.getString("reviewCreatedAt"),
                            rs.getString("reviewImgUrlOne"),
                            rs.getString("reviewImgUrlTwo"),
                            rs.getString("reviewContents"),
                            rs.getString("menuName"),
                            rs.getString("managerReviewCreatedAt"),
                            rs.getString("callUserName"),
                            rs.getString("managerReviewContents")
                    ), getReviewsParams1
            )
        );

    }
    // 사진 리뷰 조회 필터
    public GetReviewsRes getReviewsByImg(int restaurantIdx, String reviewImgStatus){
        String Query1 = "SELECT R.restaurantName, round(SUM(Rv.reviewStar)/COUNT(Rv.reviewIdx),1) as starAvg, concat('리뷰 ', COUNT(Rv.reviewIdx), '개') as reviewCount \n" +
                "FROM RC_coupang_eats_d_Riley.Review Rv \n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON Rv.restaurantIdx = R.restaurantIdx \n" +
                "where R.restaurantIdx = ?\n" +
                "GROUP BY Rv.restaurantIdx;";
        String Query2 = "SELECT replace(U.userName, SUBSTRING(U.userName,2,2), '**') as userName, Rv.reviewStar, case\n" +
                "when date(Rv.createdAt) = curdate() then '오늘' when date(Rv.createdAt) = curdate()-1 then '1일 전'\n" +
                "when date(Rv.createdAt) = curdate()-2 then '2일 전' when date(Rv.createdAt) = curdate()-3 then '3일 전'\n" +
                "when date(Rv.createdAt) = curdate()-4 then '4일 전' when date(Rv.createdAt) = curdate()-5 then '5일 전'\n" +
                "when date(Rv.createdAt) = curdate()-6 then '6일 전'\n" +
                "when date(Rv.createdAt) <= curdate()-7 and date(Rv.createdAt) > curdate()-14 then '지난 주'\n" +
                "when date(Rv.createdAt) <= curdate()-14 and date(Rv.createdAt) > curdate()-21 then '2주 전'\n" +
                "when date(Rv.createdAt) <= curdate()-21 and date(Rv.createdAt) > curdate()-28 then '3주 전'\n" +
                "when date(Rv.createdAt) <= curdate()-7 and date(Rv.createdAt) > curdate()-14 then '1달 전'\n" +
                "else '1달 전' end as reviewCreatedAt\n" +
                ", Rv.reviewImgUrlOne, Rv.reviewImgUrlTwo, Rv.reviewContents, (SELECT GROUP_CONCAT(M.menuName separator '·')) as menuName,\n" +
                "case\n" +
                "when date(MR.createdAt) = curdate() then '오늘'\n" +
                "when date(MR.createdAt) = curdate()-1 then '1일 전'\n" +
                "when date(MR.createdAt) = curdate()-2 then '2일 전'\n" +
                "when date(MR.createdAt) = curdate()-3 then '3일 전'\n" +
                "when date(MR.createdAt) = curdate()-4 then '4일 전'\n" +
                "when date(MR.createdAt) = curdate()-5 then '5일 전'\n" +
                "when date(MR.createdAt) = curdate()-6 then '6일 전'\n" +
                "when date(MR.createdAt) <= curdate()-7 and date(MR.createdAt) > curdate()-14 then '지난 주'\n" +
                "when date(MR.createdAt) <= curdate()-14 and date(MR.createdAt) > curdate()-21 then '2주 전'\n" +
                "when date(MR.createdAt) <= curdate()-21 and date(MR.createdAt) > curdate()-28 then '3주 전'\n" +
                "when date(MR.createdAt) <= curdate()-7 and date(MR.createdAt) > curdate()-14 then '1달 전'\n" +
                "else '1달 전' end as managerReviewCreatedAt,\n" +
                "replace(U.userName, SUBSTRING(U.userName,2,2), '**님,') as callUserName, MR.managerReviewContents\n" +
                "FROM RC_coupang_eats_d_Riley.Review Rv\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.User U on Rv.userIdx = U.userIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.ManagerReview MR on Rv.reviewIdx = MR.reviewIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.Order O on Rv.orderIdx = O.orderIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.Cart C on O.cartIdx = C.cartIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.CartedMenu CM on C.cartIdx = CM.cartIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.Menu M on CM.menuIdx = M.menuIdx\n" +
                "where Rv.restaurantIdx = ? and Rv.reviewImgStatus = ?\n" +
                "group by O.orderIdx;";

        int getReviewsParams1 = restaurantIdx;
        String getReviewsParams2 = reviewImgStatus;

        return new GetReviewsRes(
                getReviewRes1 = this.jdbcTemplate.queryForObject(Query1,
                        (rs, rowNum)-> new GetReviewRes1(
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount")
                        ), getReviewsParams1
                ),
                getReviewRes2 = this.jdbcTemplate.query(Query2,
                        (rs, rowNum)-> new GetReviewRes2(
                                rs.getString("userName"),
                                rs.getDouble("reviewStar"),
                                rs.getString("reviewCreatedAt"),
                                rs.getString("reviewImgUrlOne"),
                                rs.getString("reviewImgUrlTwo"),
                                rs.getString("reviewContents"),
                                rs.getString("menuName"),
                                rs.getString("managerReviewCreatedAt"),
                                rs.getString("callUserName"),
                                rs.getString("managerReviewContents")
                        ), getReviewsParams1, getReviewsParams2
                )
        );

    }
    // 최신순 정렬 필터
    public GetReviewsRes getReviewsBySorting(int restaurantIdx){
        String Query1 = "SELECT R.restaurantName, round(SUM(Rv.reviewStar)/COUNT(Rv.reviewIdx),1) as starAvg, concat('리뷰 ', COUNT(Rv.reviewIdx), '개') as reviewCount \n" +
                "FROM RC_coupang_eats_d_Riley.Review Rv \n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON Rv.restaurantIdx = R.restaurantIdx \n" +
                "where R.restaurantIdx = ?\n" +
                "GROUP BY Rv.restaurantIdx;";
        String Query2 = "SELECT replace(U.userName, SUBSTRING(U.userName,2,2), '**') as userName, Rv.reviewStar, case\n" +
                "when date(Rv.createdAt) = curdate() then '오늘' when date(Rv.createdAt) = curdate()-1 then '1일 전'\n" +
                "when date(Rv.createdAt) = curdate()-2 then '2일 전' when date(Rv.createdAt) = curdate()-3 then '3일 전'\n" +
                "when date(Rv.createdAt) = curdate()-4 then '4일 전' when date(Rv.createdAt) = curdate()-5 then '5일 전'\n" +
                "when date(Rv.createdAt) = curdate()-6 then '6일 전'\n" +
                "when date(Rv.createdAt) <= curdate()-7 and date(Rv.createdAt) > curdate()-14 then '지난 주'\n" +
                "when date(Rv.createdAt) <= curdate()-14 and date(Rv.createdAt) > curdate()-21 then '2주 전'\n" +
                "when date(Rv.createdAt) <= curdate()-21 and date(Rv.createdAt) > curdate()-28 then '3주 전'\n" +
                "when date(Rv.createdAt) <= curdate()-7 and date(Rv.createdAt) > curdate()-14 then '1달 전'\n" +
                "else '1달 전' end as reviewCreatedAt\n" +
                ", Rv.reviewImgUrlOne, Rv.reviewImgUrlTwo, Rv.reviewContents, (SELECT GROUP_CONCAT(M.menuName separator '·')) as menuName,\n" +
                "case\n" +
                "when date(MR.createdAt) = curdate() then '오늘'\n" +
                "when date(MR.createdAt) = curdate()-1 then '1일 전'\n" +
                "when date(MR.createdAt) = curdate()-2 then '2일 전'\n" +
                "when date(MR.createdAt) = curdate()-3 then '3일 전'\n" +
                "when date(MR.createdAt) = curdate()-4 then '4일 전'\n" +
                "when date(MR.createdAt) = curdate()-5 then '5일 전'\n" +
                "when date(MR.createdAt) = curdate()-6 then '6일 전'\n" +
                "when date(MR.createdAt) <= curdate()-7 and date(MR.createdAt) > curdate()-14 then '지난 주'\n" +
                "when date(MR.createdAt) <= curdate()-14 and date(MR.createdAt) > curdate()-21 then '2주 전'\n" +
                "when date(MR.createdAt) <= curdate()-21 and date(MR.createdAt) > curdate()-28 then '3주 전'\n" +
                "when date(MR.createdAt) <= curdate()-7 and date(MR.createdAt) > curdate()-14 then '1달 전'\n" +
                "else '1달 전' end as managerReviewCreatedAt,\n" +
                "replace(U.userName, SUBSTRING(U.userName,2,2), '**님,') as callUserName, MR.managerReviewContents\n" +
                "FROM RC_coupang_eats_d_Riley.Review Rv\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.User U on Rv.userIdx = U.userIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.ManagerReview MR on Rv.reviewIdx = MR.reviewIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.Order O on Rv.orderIdx = O.orderIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.Cart C on O.cartIdx = C.cartIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.CartedMenu CM on C.cartIdx = CM.cartIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.Menu M on CM.menuIdx = M.menuIdx\n" +
                "where Rv.restaurantIdx = ?\n" +
                "group by O.orderIdx\n" +
                "order by Rv.createdAt;";

        int getReviewsParams1 = restaurantIdx;

        return new GetReviewsRes(
                getReviewRes1 = this.jdbcTemplate.queryForObject(Query1,
                        (rs, rowNum)-> new GetReviewRes1(
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount")
                        ), getReviewsParams1
                ),
                getReviewRes2 = this.jdbcTemplate.query(Query2,
                        (rs, rowNum)-> new GetReviewRes2(
                                rs.getString("userName"),
                                rs.getDouble("reviewStar"),
                                rs.getString("reviewCreatedAt"),
                                rs.getString("reviewImgUrlOne"),
                                rs.getString("reviewImgUrlTwo"),
                                rs.getString("reviewContents"),
                                rs.getString("menuName"),
                                rs.getString("managerReviewCreatedAt"),
                                rs.getString("callUserName"),
                                rs.getString("managerReviewContents")
                        ), getReviewsParams1
                )
        );

    }
    // 사진 리뷰와 최신순 정렬 필터 모두 적용
    public GetReviewsRes getReviewsByImgAndSorting(int restaurantIdx, String reviewImgStatus){
        String Query1 = "SELECT R.restaurantName, round(SUM(Rv.reviewStar)/COUNT(Rv.reviewIdx),1) as starAvg, concat('리뷰 ', COUNT(Rv.reviewIdx), '개') as reviewCount \n" +
                "FROM RC_coupang_eats_d_Riley.Review Rv \n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON Rv.restaurantIdx = R.restaurantIdx \n" +
                "where R.restaurantIdx = ?\n" +
                "GROUP BY Rv.restaurantIdx;";
        String Query2 = "SELECT replace(U.userName, SUBSTRING(U.userName,2,2), '**') as userName, Rv.reviewStar, case\n" +
                "when date(Rv.createdAt) = curdate() then '오늘' when date(Rv.createdAt) = curdate()-1 then '1일 전'\n" +
                "when date(Rv.createdAt) = curdate()-2 then '2일 전' when date(Rv.createdAt) = curdate()-3 then '3일 전'\n" +
                "when date(Rv.createdAt) = curdate()-4 then '4일 전' when date(Rv.createdAt) = curdate()-5 then '5일 전'\n" +
                "when date(Rv.createdAt) = curdate()-6 then '6일 전'\n" +
                "when date(Rv.createdAt) <= curdate()-7 and date(Rv.createdAt) > curdate()-14 then '지난 주'\n" +
                "when date(Rv.createdAt) <= curdate()-14 and date(Rv.createdAt) > curdate()-21 then '2주 전'\n" +
                "when date(Rv.createdAt) <= curdate()-21 and date(Rv.createdAt) > curdate()-28 then '3주 전'\n" +
                "when date(Rv.createdAt) <= curdate()-7 and date(Rv.createdAt) > curdate()-14 then '1달 전'\n" +
                "else '1달 전' end as reviewCreatedAt\n" +
                ", Rv.reviewImgUrlOne, Rv.reviewImgUrlTwo, Rv.reviewContents, (SELECT GROUP_CONCAT(M.menuName separator '·')) as menuName,\n" +
                "case\n" +
                "when date(MR.createdAt) = curdate() then '오늘'\n" +
                "when date(MR.createdAt) = curdate()-1 then '1일 전'\n" +
                "when date(MR.createdAt) = curdate()-2 then '2일 전'\n" +
                "when date(MR.createdAt) = curdate()-3 then '3일 전'\n" +
                "when date(MR.createdAt) = curdate()-4 then '4일 전'\n" +
                "when date(MR.createdAt) = curdate()-5 then '5일 전'\n" +
                "when date(MR.createdAt) = curdate()-6 then '6일 전'\n" +
                "when date(MR.createdAt) <= curdate()-7 and date(MR.createdAt) > curdate()-14 then '지난 주'\n" +
                "when date(MR.createdAt) <= curdate()-14 and date(MR.createdAt) > curdate()-21 then '2주 전'\n" +
                "when date(MR.createdAt) <= curdate()-21 and date(MR.createdAt) > curdate()-28 then '3주 전'\n" +
                "when date(MR.createdAt) <= curdate()-7 and date(MR.createdAt) > curdate()-14 then '1달 전'\n" +
                "else '1달 전' end as managerReviewCreatedAt,\n" +
                "replace(U.userName, SUBSTRING(U.userName,2,2), '**님,') as callUserName, MR.managerReviewContents\n" +
                "FROM RC_coupang_eats_d_Riley.Review Rv\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.User U on Rv.userIdx = U.userIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.ManagerReview MR on Rv.reviewIdx = MR.reviewIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.Order O on Rv.orderIdx = O.orderIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.Cart C on O.cartIdx = C.cartIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.CartedMenu CM on C.cartIdx = CM.cartIdx\n" +
                "    LEFT JOIN RC_coupang_eats_d_Riley.Menu M on CM.menuIdx = M.menuIdx\n" +
                "where Rv.restaurantIdx = ? and Rv.reviewImgStatus = ?\n" +
                "group by O.orderIdx\n" +
                "order by Rv.createdAt;";

        int getReviewsParams1 = restaurantIdx;
        String getReviewsParams2 = reviewImgStatus;

        return new GetReviewsRes(
                getReviewRes1 = this.jdbcTemplate.queryForObject(Query1,
                        (rs, rowNum)-> new GetReviewRes1(
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount")
                        ), getReviewsParams1
                ),
                getReviewRes2 = this.jdbcTemplate.query(Query2,
                        (rs, rowNum)-> new GetReviewRes2(
                                rs.getString("userName"),
                                rs.getDouble("reviewStar"),
                                rs.getString("reviewCreatedAt"),
                                rs.getString("reviewImgUrlOne"),
                                rs.getString("reviewImgUrlTwo"),
                                rs.getString("reviewContents"),
                                rs.getString("menuName"),
                                rs.getString("managerReviewCreatedAt"),
                                rs.getString("callUserName"),
                                rs.getString("managerReviewContents")
                        ), getReviewsParams1, getReviewsParams2
                )
        );

    }
    /**
     * 카테고리 조회 API
     * [GET] /categories
     * @return BaseResponse<GetCategoryRes>
     */
    public List<GetCategoryRes> getCategory(){

        // 카테고리 리스트
        String getCategoryQuery = "SELECT RC.restaurantBigCategoryImgUrl, RC.restaurantCategoryName FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";

        return this.jdbcTemplate.query(getCategoryQuery,
                        (rs, rowNum) -> new GetCategoryRes(
                                rs.getString("restaurantBigCategoryImgUrl"),
                                rs.getString("restaurantCategoryName")

                        )
        );
    }




}
