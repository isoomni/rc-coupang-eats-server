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
    private List<GetPromoBannerImgListRes> getPromoBannerImgListRes;
    private List<GetRestaurantCategoryListRes> getRestaurantCategoryListRes;
    private List<GetPopularFranchiseRes> getPopularFranchiseRes;
    private GetCouponBannerImgListRes getCouponBannerImgListRes;
    private List<GetNewRestaurantsListRes> getNewRestaurantsListRes;
    private List<GetFilteredRes> getFilteredRes;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public GetHomeRes getHome(int deliveryAddressIdx){

        // 프로모션 배너
        String getHomeQuery1 = "SELECT PBIU.promoBannerImgUrl FROM RC_coupang_eats_d_Riley.PromoBannerImgUrl PBIU WHERE status = 'Y'";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";
        // 인기 프랜차이즈
        String getHomeQuery3 = "SELECT R.restaurantProfileUrl, R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount, concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance, if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                WHERE R.franchiseStatus ='Y' and DA.deliveryAddressIdx = ? GROUP BY RV.restaurantIdx HAVING realDistance <= 10;";
        // 쿠팡잇츠 쿠폰 배너
        String getHomeQuery4 = "SELECT CBIU.couponBannerImgUrl FROM RC_coupang_eats_d_Riley.CouponBannerImgUrl CBIU";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount,\n" +
                "       concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,\n" +
                "       if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                where R.createdAt >= date_add(now(), interval -7 day) and DA.deliveryAddressIdx = ?\n" +
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
                "where DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY starAvg;";

        int getHomeByFilterParams1 = deliveryAddressIdx;

        return new GetHomeRes(

                getPromoBannerImgListRes = this.jdbcTemplate.query(getHomeQuery1,
                        (rs, rowNum) -> new GetPromoBannerImgListRes(
                                rs.getString("promoBannerImgUrl")
                        )),
                getRestaurantCategoryListRes = this.jdbcTemplate.query(getHomeQuery2,
                        (rs, rowNum) -> new GetRestaurantCategoryListRes(
                                rs.getString("restaurantCategoryName"),
                                rs.getString("restaurantCategoryImgUrl")
                        )),
                getPopularFranchiseRes = this.jdbcTemplate.query(getHomeQuery3,
                        (rs, rowNum)-> new GetPopularFranchiseRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")

                        ), getHomeByFilterParams1),
                getCouponBannerImgListRes = this.jdbcTemplate.queryForObject(getHomeQuery4,
                        (rs, rowNum) -> new GetCouponBannerImgListRes(
                                rs.getString("couponBannerImgUrl")
                        )),
                getNewRestaurantsListRes = this.jdbcTemplate.query(getHomeQuery5,
                        (rs, rowNum)-> new GetNewRestaurantsListRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")
                        ),getHomeByFilterParams1),
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
                        ), getHomeByFilterParams1
                ));
    }
    // 치타
    public GetHomeRes getHomeByChitaFilter(int deliveryAddressIdx, String chitaDeliveryStatus){

        // 프로모션 배너
        String getHomeQuery1 = "SELECT PBIU.promoBannerImgUrl FROM RC_coupang_eats_d_Riley.PromoBannerImgUrl PBIU WHERE status = 'Y'";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";
        // 인기 프랜차이즈
        String getHomeQuery3 = "SELECT R.restaurantProfileUrl, R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount, concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance, if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                WHERE R.franchiseStatus ='Y' and DA.deliveryAddressIdx = ? GROUP BY RV.restaurantIdx HAVING realDistance <= 10;\n";
        // 쿠팡잇츠 쿠폰 배너
        String getHomeQuery4 = "SELECT CBIU.couponBannerImgUrl FROM RC_coupang_eats_d_Riley.CouponBannerImgUrl CBIU";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount,\n" +
                "       concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,\n" +
                "       if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                where R.createdAt >= date_add(now(), interval -7 day) and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY realDistance;";

        // 골라먹는 맛집
        String getHomeQuery6 = "SELECT R.restaurantProfileUrl, R.subProfileImgOne, R.subProfileImgTwo, R.restaurantName, R.chitaDeliveryStatus,\n" +
                "CONCAT(R.deliveryTime,'-',(R.deliveryTime+10),'분') as deliveryTime, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "COUNT(RV.reviewIdx) as reviewCount, concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "-radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,  if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee, C.couponName\n" +
                "FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx\n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.Coupon C on R.restaurantIdx = C.restaurantIdx,\n" +
                "RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "where R.chitaDeliveryStatus = ? AND DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY starAvg;";

        int getHomeByFilterParams1 = deliveryAddressIdx;
        String getHomeByFilterParams2= chitaDeliveryStatus;

        return new GetHomeRes(
                getPromoBannerImgListRes = this.jdbcTemplate.query(getHomeQuery1,
                        (rs, rowNum) -> new GetPromoBannerImgListRes(
                                rs.getString("promoBannerImgUrl")
                        )),
                getRestaurantCategoryListRes = this.jdbcTemplate.query(getHomeQuery2,
                        (rs, rowNum) -> new GetRestaurantCategoryListRes(
                                rs.getString("restaurantCategoryName"),
                                rs.getString("restaurantCategoryImgUrl")
                        )),
                getPopularFranchiseRes = this.jdbcTemplate.query(getHomeQuery3,
                        (rs, rowNum)-> new GetPopularFranchiseRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")

                        ),getHomeByFilterParams1),
                getCouponBannerImgListRes = this.jdbcTemplate.queryForObject(getHomeQuery4,
                        (rs, rowNum) -> new GetCouponBannerImgListRes(
                                rs.getString("couponBannerImgUrl")
                        )),
                getNewRestaurantsListRes = this.jdbcTemplate.query(getHomeQuery5,
                        (rs, rowNum)-> new GetNewRestaurantsListRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")
                        ), getHomeByFilterParams1),
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
                        ), getHomeByFilterParams2, getHomeByFilterParams1
                ));
    }

    // 쿠폰
    public GetHomeRes getHomeByCouponFilter(int deliveryAddressIdx, String couponStatus){

        // 프로모션 배너
        String getHomeQuery1 = "SELECT PBIU.promoBannerImgUrl FROM RC_coupang_eats_d_Riley.PromoBannerImgUrl PBIU WHERE status = 'Y'";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";
        // 인기 프랜차이즈
        String getHomeQuery3 = "SELECT R.restaurantProfileUrl, R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount, concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance, if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                WHERE R.franchiseStatus ='Y' and DA.deliveryAddressIdx = ? GROUP BY RV.restaurantIdx HAVING realDistance <= 10;\n";
        // 쿠팡잇츠 쿠폰 배너
        String getHomeQuery4 = "SELECT CBIU.couponBannerImgUrl FROM RC_coupang_eats_d_Riley.CouponBannerImgUrl CBIU";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount,\n" +
                "       concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,\n" +
                "       if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                where R.createdAt >= date_add(now(), interval -7 day) and DA.deliveryAddressIdx = ?\n" +
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
                "where R.couponStatus = ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY starAvg;";


        String getHomeByFilterParams2 = couponStatus;
        int getHomeByFilterParams1 = deliveryAddressIdx;

        return new GetHomeRes(
                getPromoBannerImgListRes = this.jdbcTemplate.query(getHomeQuery1,
                        (rs, rowNum) -> new GetPromoBannerImgListRes(
                                rs.getString("promoBannerImgUrl")
                        )),
                getRestaurantCategoryListRes = this.jdbcTemplate.query(getHomeQuery2,
                        (rs, rowNum) -> new GetRestaurantCategoryListRes(
                                rs.getString("restaurantCategoryName"),
                                rs.getString("restaurantCategoryImgUrl")
                        )),
                getPopularFranchiseRes = this.jdbcTemplate.query(getHomeQuery3,
                        (rs, rowNum)-> new GetPopularFranchiseRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")

                        ), getHomeByFilterParams1),
                getCouponBannerImgListRes = this.jdbcTemplate.queryForObject(getHomeQuery4,
                        (rs, rowNum) -> new GetCouponBannerImgListRes(
                                rs.getString("couponBannerImgUrl")
                        )),
                getNewRestaurantsListRes = this.jdbcTemplate.query(getHomeQuery5,
                        (rs, rowNum)-> new GetNewRestaurantsListRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")
                        ), getHomeByFilterParams1),
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
                        ), getHomeByFilterParams2, getHomeByFilterParams1
                ));
    }
    // 최소 주문
    public GetHomeRes getHomeByMinDeliveryAmountFilter(int deliveryAddressIdx, Double minDeliveryAmount){

        // 프로모션 배너
        String getHomeQuery1 = "SELECT PBIU.promoBannerImgUrl FROM RC_coupang_eats_d_Riley.PromoBannerImgUrl PBIU WHERE status = 'Y'";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";
        // 인기 프랜차이즈
        String getHomeQuery3 = "SELECT R.restaurantProfileUrl, R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount, concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance, if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                WHERE R.franchiseStatus ='Y' and DA.deliveryAddressIdx = ? GROUP BY RV.restaurantIdx HAVING realDistance <= 10;\n";
        // 쿠팡잇츠 쿠폰 배너
        String getHomeQuery4 = "SELECT CBIU.couponBannerImgUrl FROM RC_coupang_eats_d_Riley.CouponBannerImgUrl CBIU";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount,\n" +
                "       concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,\n" +
                "       if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                where R.createdAt >= date_add(now(), interval -7 day) and DA.deliveryAddressIdx = ?\n" +
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
                "where R.minDeliveryAmount <= ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY starAvg;";


        Double getHomeByFilterParams2 = minDeliveryAmount;
        int getHomeByFilterParams1 = deliveryAddressIdx;

        return new GetHomeRes(
                getPromoBannerImgListRes = this.jdbcTemplate.query(getHomeQuery1,
                        (rs, rowNum) -> new GetPromoBannerImgListRes(
                                rs.getString("promoBannerImgUrl")
                        )),
                getRestaurantCategoryListRes = this.jdbcTemplate.query(getHomeQuery2,
                        (rs, rowNum) -> new GetRestaurantCategoryListRes(
                                rs.getString("restaurantCategoryName"),
                                rs.getString("restaurantCategoryImgUrl")
                        )),
                getPopularFranchiseRes = this.jdbcTemplate.query(getHomeQuery3,
                        (rs, rowNum)-> new GetPopularFranchiseRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")

                        ), getHomeByFilterParams1),
                getCouponBannerImgListRes = this.jdbcTemplate.queryForObject(getHomeQuery4,
                        (rs, rowNum) -> new GetCouponBannerImgListRes(
                                rs.getString("couponBannerImgUrl")
                        )),
                getNewRestaurantsListRes = this.jdbcTemplate.query(getHomeQuery5,
                        (rs, rowNum)-> new GetNewRestaurantsListRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")
                        ), getHomeByFilterParams1),
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
                        ), getHomeByFilterParams2, getHomeByFilterParams1
                ));
    }

    // 치타 배달 & 최소 주문
    public GetHomeRes getHomeByChitaAndMinFilter(int deliveryAddressIdx, String chitaDeliveryStatus, Double minDeliveryAmount){

        // 프로모션 배너
        String getHomeQuery1 = "SELECT PBIU.promoBannerImgUrl FROM RC_coupang_eats_d_Riley.PromoBannerImgUrl PBIU WHERE status = 'Y'";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";
        // 인기 프랜차이즈
        String getHomeQuery3 = "SELECT R.restaurantProfileUrl, R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount, concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance, if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                WHERE R.franchiseStatus ='Y' and DA.deliveryAddressIdx = ? GROUP BY RV.restaurantIdx HAVING realDistance <= 10;\n";
        // 쿠팡잇츠 쿠폰 배너
        String getHomeQuery4 = "SELECT CBIU.couponBannerImgUrl FROM RC_coupang_eats_d_Riley.CouponBannerImgUrl CBIU";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount,\n" +
                "       concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,\n" +
                "       if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                where R.createdAt >= date_add(now(), interval -7 day) and DA.deliveryAddressIdx = ?\n" +
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
                "where R.chitaDeliveryStatus = ? and R.minDeliveryAmount <= ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY starAvg;";


        String getHomeByFilterParams1 = chitaDeliveryStatus;
        Double getHomeByFilterParams2 = minDeliveryAmount;
        int getHomeByFilterParams3 = deliveryAddressIdx;

        return new GetHomeRes(
                getPromoBannerImgListRes = this.jdbcTemplate.query(getHomeQuery1,
                        (rs, rowNum) -> new GetPromoBannerImgListRes(
                                rs.getString("promoBannerImgUrl")
                        )),
                getRestaurantCategoryListRes = this.jdbcTemplate.query(getHomeQuery2,
                        (rs, rowNum) -> new GetRestaurantCategoryListRes(
                                rs.getString("restaurantCategoryName"),
                                rs.getString("restaurantCategoryImgUrl")
                        )),
                getPopularFranchiseRes = this.jdbcTemplate.query(getHomeQuery3,
                        (rs, rowNum)-> new GetPopularFranchiseRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")

                        ), getHomeByFilterParams3),
                getCouponBannerImgListRes = this.jdbcTemplate.queryForObject(getHomeQuery4,
                        (rs, rowNum) -> new GetCouponBannerImgListRes(
                                rs.getString("couponBannerImgUrl")
                        )),
                getNewRestaurantsListRes = this.jdbcTemplate.query(getHomeQuery5,
                        (rs, rowNum)-> new GetNewRestaurantsListRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")
                        ), getHomeByFilterParams3),
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
                        ), getHomeByFilterParams1, getHomeByFilterParams2 ,getHomeByFilterParams3
                        ));
    }
    // 치타 배달 & 쿠폰
    public GetHomeRes getHomeByChitaAndCouponFilter(int deliveryAddressIdx, String chitaDeliveryStatus, String couponStatus){

        // 프로모션 배너
        String getHomeQuery1 = "SELECT PBIU.promoBannerImgUrl FROM RC_coupang_eats_d_Riley.PromoBannerImgUrl PBIU WHERE status = 'Y'";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";
        // 인기 프랜차이즈
        String getHomeQuery3 = "SELECT R.restaurantProfileUrl, R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount, concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance, if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                WHERE R.franchiseStatus ='Y' and DA.deliveryAddressIdx = ? GROUP BY RV.restaurantIdx HAVING realDistance <= 10;\n";
        // 쿠팡잇츠 쿠폰 배너
        String getHomeQuery4 = "SELECT CBIU.couponBannerImgUrl FROM RC_coupang_eats_d_Riley.CouponBannerImgUrl CBIU";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount,\n" +
                "       concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,\n" +
                "       if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                where R.createdAt >= date_add(now(), interval -7 day) and DA.deliveryAddressIdx = ?\n" +
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
                "where R.chitaDeliveryStatus = ? and R.couponStatus = ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY starAvg;";


        String getHomeByFilterParams1 = chitaDeliveryStatus;
        String getHomeByFilterParams2 = couponStatus;
        int getHomeByFilterParams3 = deliveryAddressIdx;

        return new GetHomeRes(
                getPromoBannerImgListRes = this.jdbcTemplate.query(getHomeQuery1,
                        (rs, rowNum) -> new GetPromoBannerImgListRes(
                                rs.getString("promoBannerImgUrl")
                        )),
                getRestaurantCategoryListRes = this.jdbcTemplate.query(getHomeQuery2,
                        (rs, rowNum) -> new GetRestaurantCategoryListRes(
                                rs.getString("restaurantCategoryName"),
                                rs.getString("restaurantCategoryImgUrl")
                        )),
                getPopularFranchiseRes = this.jdbcTemplate.query(getHomeQuery3,
                        (rs, rowNum)-> new GetPopularFranchiseRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")

                        ), getHomeByFilterParams3),
                getCouponBannerImgListRes = this.jdbcTemplate.queryForObject(getHomeQuery4,
                        (rs, rowNum) -> new GetCouponBannerImgListRes(
                                rs.getString("couponBannerImgUrl")
                        )),
                getNewRestaurantsListRes = this.jdbcTemplate.query(getHomeQuery5,
                        (rs, rowNum)-> new GetNewRestaurantsListRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")
                        ), getHomeByFilterParams3),
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
                        ),getHomeByFilterParams1, getHomeByFilterParams2, getHomeByFilterParams3
                ));
    }
    // 쿠폰 & 최소 주문
    public GetHomeRes getHomeByCouponAndMinFilter(int deliveryAddressIdx, Double minDeliveryAmount, String couponStatus){

        // 프로모션 배너
        String getHomeQuery1 = "SELECT PBIU.promoBannerImgUrl FROM RC_coupang_eats_d_Riley.PromoBannerImgUrl PBIU WHERE status = 'Y'";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";
        // 인기 프랜차이즈
        String getHomeQuery3 = "SELECT R.restaurantProfileUrl, R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount, concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance, if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                WHERE R.franchiseStatus ='Y' and DA.deliveryAddressIdx = ? GROUP BY RV.restaurantIdx HAVING realDistance <= 10;\n";
        // 쿠팡잇츠 쿠폰 배너
        String getHomeQuery4 = "SELECT CBIU.couponBannerImgUrl FROM RC_coupang_eats_d_Riley.CouponBannerImgUrl CBIU";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount,\n" +
                "       concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,\n" +
                "       if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                where R.createdAt >= date_add(now(), interval -7 day) and DA.deliveryAddressIdx = ?\n" +
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
                "where R.minDeliveryAmount <= ? and R.couponStatus = ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY starAvg;";


        Double getHomeByFilterParams1 = minDeliveryAmount;
        String getHomeByFilterParams2 = couponStatus;
        int getHomeByFilterParams3 = deliveryAddressIdx;

        return new GetHomeRes(
                getPromoBannerImgListRes = this.jdbcTemplate.query(getHomeQuery1,
                        (rs, rowNum) -> new GetPromoBannerImgListRes(
                                rs.getString("promoBannerImgUrl")
                        )),
                getRestaurantCategoryListRes = this.jdbcTemplate.query(getHomeQuery2,
                        (rs, rowNum) -> new GetRestaurantCategoryListRes(
                                rs.getString("restaurantCategoryName"),
                                rs.getString("restaurantCategoryImgUrl")
                        )),
                getPopularFranchiseRes = this.jdbcTemplate.query(getHomeQuery3,
                        (rs, rowNum)-> new GetPopularFranchiseRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")

                        ), getHomeByFilterParams3),
                getCouponBannerImgListRes = this.jdbcTemplate.queryForObject(getHomeQuery4,
                        (rs, rowNum) -> new GetCouponBannerImgListRes(
                                rs.getString("couponBannerImgUrl")
                        )),
                getNewRestaurantsListRes = this.jdbcTemplate.query(getHomeQuery5,
                        (rs, rowNum)-> new GetNewRestaurantsListRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")
                        ), getHomeByFilterParams3),
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
                        ),getHomeByFilterParams1, getHomeByFilterParams2, getHomeByFilterParams3
                ));
    }
    public GetHomeRes getHomeByFilter(int deliveryAddressIdx, String chitaDeliveryStatus, String couponStatus, Double minDeliveryAmount){

        // 프로모션 배너
        String getHomeQuery1 = "SELECT PBIU.promoBannerImgUrl FROM RC_coupang_eats_d_Riley.PromoBannerImgUrl PBIU WHERE status = 'Y'";
        // 카테고리 리스트
        String getHomeQuery2 = "SELECT RC.restaurantCategoryName, RC.restaurantCategoryImgUrl FROM RC_coupang_eats_d_Riley.RestaurantCategory RC";
        // 인기 프랜차이즈
        String getHomeQuery3 = "SELECT R.restaurantProfileUrl, R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount, concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance, if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                WHERE R.franchiseStatus ='Y' and DA.deliveryAddressIdx = ? GROUP BY RV.restaurantIdx HAVING realDistance <= 10;\n";
        // 쿠팡잇츠 쿠폰 배너
        String getHomeQuery4 = "SELECT CBIU.couponBannerImgUrl FROM RC_coupang_eats_d_Riley.CouponBannerImgUrl CBIU";

        // 새로 들어왔어요
        String getHomeQuery5 = "SELECT R.restaurantProfileUrl,R.restaurantName, round(SUM(RV.reviewStar)/COUNT(RV.reviewIdx),1) as starAvg,\n" +
                "                COUNT(RV.reviewIdx) as reviewCount,\n" +
                "       concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km') AS realDistance,\n" +
                "       if(R.deliveryFee=0, '무료배달', concat(format(R.deliveryFee, 0), '원')) as deliveryFee\n" +
                "                FROM RC_coupang_eats_d_Riley.Review RV LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON RV.restaurantIdx =R.restaurantIdx,\n" +
                "                     RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "                where R.createdAt >= date_add(now(), interval -7 day) and DA.deliveryAddressIdx = ?\n" +
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
                "where R.chitaDeliveryStatus = ? and R.couponStatus = ? and R.minDeliveryAmount <= ? and DA.deliveryAddressIdx = ?\n" +
                "GROUP BY RV.restaurantIdx\n" +
                "HAVING realDistance <= 10\n" +
                "ORDER BY starAvg;";

        String getHomeByFilterParams1 = chitaDeliveryStatus;
        String getHomeByFilterParams2 = couponStatus;
        Double getHomeByFilterParams3 = minDeliveryAmount;
        int getHomeByFilterParams4 = deliveryAddressIdx;

        return new GetHomeRes(
                getPromoBannerImgListRes = this.jdbcTemplate.query(getHomeQuery1,
                        (rs, rowNum) -> new GetPromoBannerImgListRes(
                                rs.getString("promoBannerImgUrl")
                        )),
                getRestaurantCategoryListRes = this.jdbcTemplate.query(getHomeQuery2,
                        (rs, rowNum) -> new GetRestaurantCategoryListRes(
                                rs.getString("restaurantCategoryName"),
                                rs.getString("restaurantCategoryImgUrl")
                        )),
                getPopularFranchiseRes = this.jdbcTemplate.query(getHomeQuery3,
                        (rs, rowNum)-> new GetPopularFranchiseRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")

                        ), getHomeByFilterParams4),
                getCouponBannerImgListRes = this.jdbcTemplate.queryForObject(getHomeQuery4,
                        (rs, rowNum) -> new GetCouponBannerImgListRes(
                                rs.getString("couponBannerImgUrl")
                        )),
                getNewRestaurantsListRes = this.jdbcTemplate.query(getHomeQuery5,
                        (rs, rowNum)-> new GetNewRestaurantsListRes(
                                rs.getString("restaurantProfileUrl"),
                                rs.getString("restaurantName"),
                                rs.getDouble("starAvg"),
                                rs.getString("reviewCount"),
                                rs.getString("realDistance"),
                                rs.getString("deliveryFee")
                        ), getHomeByFilterParams4),
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
                        ), getHomeByFilterParams1, getHomeByFilterParams2, getHomeByFilterParams3, getHomeByFilterParams4
                ));
    }






}
