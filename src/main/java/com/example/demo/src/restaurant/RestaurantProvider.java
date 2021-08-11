package com.example.demo.src.restaurant;

import com.example.demo.config.BaseException;
import com.example.demo.src.home.model.GetRestaurantCategoryListRes;
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

    /**
     * 카테고리 별 식당 조회 API
     * [GET] /restaurants/:restaurantCategoryIdx
     * @return BaseResponse<GetRestaurantRes>
     */
    public GetRestaurantRes getRestaurant(int restaurantCategoryIdx, int deliveryAddressIdx) throws BaseException{
        try{
            GetRestaurantRes getRestaurantRes = restaurantDao.getRestaurant(restaurantCategoryIdx, deliveryAddressIdx);
            return getRestaurantRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 치타
    public GetRestaurantRes getRestaurantByChitaFilter(int restaurantCategoryIdx, int deliveryAddressIdx, String chitaDeliveryStatus) throws BaseException{
        try{
            GetRestaurantRes getRestaurantRes = restaurantDao.getRestaurantByChitaFilter(restaurantCategoryIdx, deliveryAddressIdx, chitaDeliveryStatus);
            return getRestaurantRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    //쿠폰
    public GetRestaurantRes getRestaurantByCouponFilter(int restaurantCategoryIdx, int deliveryAddressIdx, String couponStatus) throws BaseException{
        try{
            GetRestaurantRes getRestaurantRes = restaurantDao.getRestaurantByCouponFilter(restaurantCategoryIdx, deliveryAddressIdx, couponStatus);
            return getRestaurantRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 최소 주문액
    public GetRestaurantRes getRestaurantByMinDeliveryAmountFilter(int restaurantCategoryIdx, int deliveryAddressIdx, Double minDeliveryAmount) throws BaseException{
        try{
            GetRestaurantRes getRestaurantRes = restaurantDao.getRestaurantByMinDeliveryAmountFilter(restaurantCategoryIdx, deliveryAddressIdx, minDeliveryAmount);
            return getRestaurantRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 치타 배달 & 최소 주문
    public GetRestaurantRes getRestaurantByChitaAndMinFilter(int restaurantCategoryIdx, int deliveryAddressIdx, String chitaDeliveryStatus, Double minDeliveryAmount) throws BaseException{
        try{
            GetRestaurantRes getRestaurantRes = restaurantDao.getRestaurantByChitaAndMinFilter(restaurantCategoryIdx, deliveryAddressIdx, chitaDeliveryStatus, minDeliveryAmount);
            return getRestaurantRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 치타 배달 & 쿠폰
    public GetRestaurantRes getRestaurantByChitaAndCouponFilter(int restaurantCategoryIdx, int deliveryAddressIdx, String chitaDeliveryStatus, String couponStatus) throws BaseException{
        try{
            GetRestaurantRes getRestaurantRes = restaurantDao.getRestaurantByChitaAndCouponFilter(restaurantCategoryIdx, deliveryAddressIdx, chitaDeliveryStatus, couponStatus);
            return getRestaurantRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 쿠폰 & 최소 주문
    public GetRestaurantRes getRestaurantByCouponAndMinFilter(int restaurantCategoryIdx, int deliveryAddressIdx, Double minDeliveryAmount, String couponStatus) throws BaseException{
        try{
            GetRestaurantRes getRestaurantRes = restaurantDao.getRestaurantByCouponAndMinFilter(restaurantCategoryIdx, deliveryAddressIdx, minDeliveryAmount, couponStatus);
            return getRestaurantRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 모두 다 있는 필터
    public GetRestaurantRes getRestaurantByFilter(int restaurantCategoryIdx, int deliveryAddressIdx, String chitaDeliveryStatus, String couponStatus, Double minDeliveryAmount) throws BaseException{
        try{
            GetRestaurantRes getRestaurantRes = restaurantDao.getRestaurantByFilter(restaurantCategoryIdx, deliveryAddressIdx, chitaDeliveryStatus, couponStatus, minDeliveryAmount);
            return getRestaurantRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /**
     * 식당 메뉴 조회 API
     * [GET] /restaurants/:restaurantIdx
     * @return BaseResponse<GetRestaurantRes>
     */

    public GetRestaurantMenuRes getRestaurantMenu(int userIdx, int restaurantIdx) throws BaseException {
        try{
            GetRestaurantMenuRes getRestaurantMenuRes = restaurantDao.getRestaurantMenu(userIdx, restaurantIdx);
            return getRestaurantMenuRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 식당 리뷰 조회 API
     * [GET] /restaurants/:restaurantIdx/review
     * @return BaseResponse<GetReviewRes>
     */
    public GetReviewsRes getReviews(int restaurantIdx) throws BaseException {
        try{
            GetReviewsRes getReviewsRes = restaurantDao.getReviews(restaurantIdx);
            return getReviewsRes;
        } catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 사진 리뷰 필터
    public GetReviewsRes getReviewsByImg(int restaurantIdx, String reviewImgStatus) throws BaseException {
        try{
            GetReviewsRes getReviewsRes = restaurantDao.getReviewsByImg(restaurantIdx, reviewImgStatus);
            return getReviewsRes;
        } catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 최신순 정렬 필터
    public GetReviewsRes getReviewsBySorting(int restaurantIdx) throws BaseException {
        try{
            GetReviewsRes getReviewsRes = restaurantDao.getReviewsBySorting(restaurantIdx);
            return getReviewsRes;
        } catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 사진 리뷰와 최신순 정렬 필터 모두 적용
    public GetReviewsRes getReviewsByImgAndSorting(int restaurantIdx, String reviewImgStatus) throws BaseException {
        try{
            GetReviewsRes getReviewsRes = restaurantDao.getReviewsByImgAndSorting(restaurantIdx, reviewImgStatus);
            return getReviewsRes;
        } catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /**
     * 카테고리 조회 API
     * [GET] /categories
     * @return BaseResponse<GetCategoryRes>
     */
    public List<GetCategoryRes> getCategory() throws BaseException{
        try{
            List<GetCategoryRes> getCategoryRes = restaurantDao.getCategory();
            return getCategoryRes;
        } catch (Exception exception){
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
