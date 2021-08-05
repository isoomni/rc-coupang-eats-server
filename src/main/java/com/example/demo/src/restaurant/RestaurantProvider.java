package com.example.demo.src.restaurant;

import com.example.demo.config.BaseException;
import com.example.demo.src.restaurant.RestaurantDao;
import com.example.demo.src.restaurant.model.GetRestaurantRes;
import com.example.demo.src.restaurant.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;


//Provider : Read의 비즈니스 로직 처리
@Service
public class RestaurantProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RestaurantDao restaurantDao;

    @Autowired
    public RestaurantProvider(RestaurantDao restaurantDao){
        this.restaurantDao = restaurantDao;
    }

    // 여기서부터 본문
    public GetRestaurantRes getRestaurant(int restaurantCategoryIdx) throws BaseException{
        try{
            GetRestaurantRes getRestaurantRes = restaurantDao.getRestaurant(restaurantCategoryIdx);
            return getRestaurantRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 치타
    public GetRestaurantRes getRestaurantByChitaFilter(int restaurantCategoryIdx, String chitaDeliveryStatus) throws BaseException{
        try{
            GetRestaurantRes getRestaurantRes = restaurantDao.getRestaurantByChitaFilter(restaurantCategoryIdx, chitaDeliveryStatus);
            return getRestaurantRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    //쿠폰
    public GetRestaurantRes getRestaurantByCouponFilter(int restaurantCategoryIdx, String couponStatus) throws BaseException{
        try{
            GetRestaurantRes getRestaurantRes = restaurantDao.getRestaurantByCouponFilter(restaurantCategoryIdx, couponStatus);
            return getRestaurantRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 최소 주문액
    public GetRestaurantRes getRestaurantByMinDeliveryAmountFilter(int restaurantCategoryIdx, Double minDeliveryAmount) throws BaseException{
        try{
            GetRestaurantRes getRestaurantRes = restaurantDao.getRestaurantByMinDeliveryAmountFilter(restaurantCategoryIdx, minDeliveryAmount);
            return getRestaurantRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 치타 배달 & 최소 주문
    public GetRestaurantRes getRestaurantByChitaAndMinFilter(int restaurantCategoryIdx, String chitaDeliveryStatus, Double minDeliveryAmount) throws BaseException{
        try{
            GetRestaurantRes getRestaurantRes = restaurantDao.getRestaurantByChitaAndMinFilter(restaurantCategoryIdx, chitaDeliveryStatus, minDeliveryAmount);
            return getRestaurantRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 치타 배달 & 쿠폰
    public GetRestaurantRes getRestaurantByChitaAndCouponFilter(int restaurantCategoryIdx, String chitaDeliveryStatus, String couponStatus) throws BaseException{
        try{
            GetRestaurantRes getRestaurantRes = restaurantDao.getRestaurantByChitaAndCouponFilter(restaurantCategoryIdx, chitaDeliveryStatus, couponStatus);
            return getRestaurantRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 쿠폰 & 최소 주문
    public GetRestaurantRes getRestaurantByCouponAndMinFilter(int restaurantCategoryIdx, Double minDeliveryAmount, String couponStatus) throws BaseException{
        try{
            GetRestaurantRes getRestaurantRes = restaurantDao.getRestaurantByCouponAndMinFilter(restaurantCategoryIdx, minDeliveryAmount, couponStatus);
            return getRestaurantRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 모두 다 있는 필터
    public GetRestaurantRes getRestaurantByFilter(int restaurantCategoryIdx, String chitaDeliveryStatus, String couponStatus, Double minDeliveryAmount) throws BaseException{
        try{
            GetRestaurantRes getRestaurantRes = restaurantDao.getRestaurantByFilter(restaurantCategoryIdx, chitaDeliveryStatus, couponStatus, minDeliveryAmount);
            return getRestaurantRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
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
