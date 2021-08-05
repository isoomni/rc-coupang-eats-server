package com.example.demo.src.restaurant;

import com.example.demo.src.restaurant.RestaurantProvider;
import com.example.demo.src.restaurant.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.restaurant.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/app/restaurants")
public class RestaurantController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final RestaurantProvider restaurantProvider;
    @Autowired
    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantProvider restaurantProvider, RestaurantService restaurantService){
        this.restaurantProvider = restaurantProvider;
        this.restaurantService = restaurantService;
    }

    /**
     * 카테고리 별 식당 조회 API
     * [GET] /restaurants/:restaurantCategoryIdx
     * @return BaseResponse<GetRestaurantRes>
     */
    @ResponseBody
    @GetMapping("/{restaurantCategoryIdx}") // (GET) 127.0.0.1:9000/app/restaurants/:restaurantCategoryIdx
    public BaseResponse<GetRestaurantRes> getRestaurant(@PathVariable("restaurantCategoryIdx") int restaurantCategoryIdx, @RequestParam(required = false) String chitaDeliveryStatus, @RequestParam(required = false) String couponStatus, @RequestParam(required = false) Double minDeliveryAmount){
        try{
            if(chitaDeliveryStatus == null && couponStatus == null && minDeliveryAmount == null){ // 치타 배달에 대한 값 혹은 쿠폰에 대한 값이 쿼리 스트링으로 전달 받지 못하는 경우에는
                GetRestaurantRes getRestaurantRes = restaurantProvider.getRestaurant(restaurantCategoryIdx); //전체 식당를 조회하고
                return new BaseResponse<>(getRestaurantRes);
            }else if (chitaDeliveryStatus != null && couponStatus == null && minDeliveryAmount == null){  // 치타 배달 값만 전달된 경우에는
                GetRestaurantRes getRestaurantRes = restaurantProvider.getRestaurantByChitaFilter(restaurantCategoryIdx, chitaDeliveryStatus); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getRestaurantRes);
            }else if (chitaDeliveryStatus == null && couponStatus != null && minDeliveryAmount == null) { // 쿠폰 값만 전달된 경우에는
                GetRestaurantRes getRestaurantRes = restaurantProvider.getRestaurantByCouponFilter(restaurantCategoryIdx, couponStatus); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getRestaurantRes);
            }else if (chitaDeliveryStatus == null && couponStatus == null && minDeliveryAmount != null) { // 최소 주문 값만 전달된 경우에는
                GetRestaurantRes getRestaurantRes = restaurantProvider.getRestaurantByMinDeliveryAmountFilter(restaurantCategoryIdx, minDeliveryAmount); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getRestaurantRes);
            }else if (chitaDeliveryStatus != null && couponStatus == null && minDeliveryAmount != null) { // 치타 배달 값, 최소 주문 값만 전달된 경우에는
                GetRestaurantRes getRestaurantRes = restaurantProvider.getRestaurantByChitaAndMinFilter(restaurantCategoryIdx, chitaDeliveryStatus, minDeliveryAmount); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getRestaurantRes);
            }else if (chitaDeliveryStatus != null && couponStatus != null && minDeliveryAmount == null) { // 치타 배달 값, 쿠폰 값만 전달된 경우에는
                GetRestaurantRes getRestaurantRes = restaurantProvider.getRestaurantByChitaAndCouponFilter(restaurantCategoryIdx, chitaDeliveryStatus, couponStatus); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getRestaurantRes);
            }else if (chitaDeliveryStatus == null && couponStatus != null && minDeliveryAmount != null) { // 쿠폰 값, 최소 주문 값만 전달된 경우에는
                GetRestaurantRes getRestaurantRes = restaurantProvider.getRestaurantByCouponAndMinFilter(restaurantCategoryIdx, minDeliveryAmount, couponStatus); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getRestaurantRes);

            } GetRestaurantRes getRestaurantRes = restaurantProvider.getRestaurantByFilter(restaurantCategoryIdx, chitaDeliveryStatus, couponStatus, minDeliveryAmount); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
            return new BaseResponse<>(getRestaurantRes);

        }  catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 로그 테스트 API
     * [GET] /test/log
     * @return String
     */
    @ResponseBody
    @GetMapping("/log")
    public String getAll() {
        System.out.println("테스트");
//        trace, debug 레벨은 Console X, 파일 로깅 X
//        logger.trace("TRACE Level 테스트");
//        logger.debug("DEBUG Level 테스트");

//        info 레벨은 Console 로깅 O, 파일 로깅 X
        logger.info("INFO Level 테스트");
//        warn 레벨은 Console 로깅 O, 파일 로깅 O
        logger.warn("Warn Level 테스트");
//        error 레벨은 Console 로깅 O, 파일 로깅 O (app.log 뿐만 아니라 error.log 에도 로깅 됨)
//        app.log 와 error.log 는 날짜가 바뀌면 자동으로 *.gz 으로 압축 백업됨
        logger.error("ERROR Level 테스트");

        return "Success Test";
    }

}
