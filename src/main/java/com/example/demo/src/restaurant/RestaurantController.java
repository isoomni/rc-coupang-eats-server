package com.example.demo.src.restaurant;

import com.example.demo.src.order.model.Order;
import com.example.demo.src.order.model.PatchOrderReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.restaurant.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;


@RestController
@RequestMapping("/app/restaurants")
public class RestaurantController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final RestaurantProvider restaurantProvider;
    @Autowired
    private final RestaurantService restaurantService;
    @Autowired
    private final JwtService jwtService;

    public RestaurantController(RestaurantProvider restaurantProvider, RestaurantService restaurantService, JwtService jwtService){
        this.restaurantProvider = restaurantProvider;
        this.restaurantService = restaurantService;
        this.jwtService = jwtService;
    }

    /**
     * 카테고리 별 식당 조회 API
     * [GET] /restaurants/:restaurantCategoryIdx/:userIdx/:deliveryAddressIdx
     * @return BaseResponse<GetRestaurantRes>
     */
    @ResponseBody
    @GetMapping("/{restaurantCategoryIdx}/{userIdx}/{deliveryAddressIdx}") // (GET) 127.0.0.1:9090/app/restaurants/:restaurantCategoryIdx/:userIdx/:deliveryAddressIdx
    public BaseResponse<GetRestaurantRes> getRestaurant(@PathVariable("restaurantCategoryIdx") int restaurantCategoryIdx, @PathVariable("userIdx") int userIdx, @PathVariable("deliveryAddressIdx") int deliveryAddressIdx, @RequestParam(required = false) String chitaDeliveryStatus, @RequestParam(required = false) String couponStatus, @RequestParam(required = false) Double minDeliveryAmount){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용
            //같다면 유저네임 변경
            if(chitaDeliveryStatus == null && couponStatus == null && minDeliveryAmount == null){ // 치타 배달에 대한 값 혹은 쿠폰에 대한 값이 쿼리 스트링으로 전달 받지 못하는 경우에는
                GetRestaurantRes getRestaurantRes = restaurantProvider.getRestaurant(restaurantCategoryIdx, deliveryAddressIdx); //전체 식당를 조회하고
                return new BaseResponse<>(getRestaurantRes);
            }else if (chitaDeliveryStatus != null && couponStatus == null && minDeliveryAmount == null){  // 치타 배달 값만 전달된 경우에는
                GetRestaurantRes getRestaurantRes = restaurantProvider.getRestaurantByChitaFilter(restaurantCategoryIdx, deliveryAddressIdx, chitaDeliveryStatus); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getRestaurantRes);
            }else if (chitaDeliveryStatus == null && couponStatus != null && minDeliveryAmount == null) { // 쿠폰 값만 전달된 경우에는
                GetRestaurantRes getRestaurantRes = restaurantProvider.getRestaurantByCouponFilter(restaurantCategoryIdx, deliveryAddressIdx, couponStatus); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getRestaurantRes);
            }else if (chitaDeliveryStatus == null && couponStatus == null && minDeliveryAmount != null) { // 최소 주문 값만 전달된 경우에는
                GetRestaurantRes getRestaurantRes = restaurantProvider.getRestaurantByMinDeliveryAmountFilter(restaurantCategoryIdx, deliveryAddressIdx, minDeliveryAmount); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getRestaurantRes);
            }else if (chitaDeliveryStatus != null && couponStatus == null && minDeliveryAmount != null) { // 치타 배달 값, 최소 주문 값만 전달된 경우에는
                GetRestaurantRes getRestaurantRes = restaurantProvider.getRestaurantByChitaAndMinFilter(restaurantCategoryIdx, deliveryAddressIdx, chitaDeliveryStatus, minDeliveryAmount); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getRestaurantRes);
            }else if (chitaDeliveryStatus != null && couponStatus != null && minDeliveryAmount == null) { // 치타 배달 값, 쿠폰 값만 전달된 경우에는
                GetRestaurantRes getRestaurantRes = restaurantProvider.getRestaurantByChitaAndCouponFilter(restaurantCategoryIdx, deliveryAddressIdx, chitaDeliveryStatus, couponStatus); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getRestaurantRes);
            }else if (chitaDeliveryStatus == null && couponStatus != null && minDeliveryAmount != null) { // 쿠폰 값, 최소 주문 값만 전달된 경우에는
                GetRestaurantRes getRestaurantRes = restaurantProvider.getRestaurantByCouponAndMinFilter(restaurantCategoryIdx, deliveryAddressIdx, minDeliveryAmount, couponStatus); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getRestaurantRes);

            } GetRestaurantRes getRestaurantRes = restaurantProvider.getRestaurantByFilter(restaurantCategoryIdx, deliveryAddressIdx, chitaDeliveryStatus, couponStatus, minDeliveryAmount); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
            return new BaseResponse<>(getRestaurantRes);

        }  catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 식당 메뉴 조회 API
     * [GET] /restaurants/:userIdx/:restaurantIdx
     * @return BaseResponse<GetRestaurantRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/{restaurantIdx}") // (GET) 127.0.0.1:9090/app/restaurants/:userIdx/:restaurantIdx
    public BaseResponse<GetRestaurantMenuRes> getRestaurantMenu(@PathVariable("userIdx") int userIdx, @PathVariable("restaurantIdx") int restaurantIdx){

        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용
            GetRestaurantMenuRes getRestaurantMenuRes = restaurantProvider.getRestaurantMenu(userIdx, restaurantIdx);
            return new BaseResponse<>(getRestaurantMenuRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 식당 리뷰 조회 API
     * [GET] /restaurants/:restaurantIdx/reviews/:userIdx
     * @return BaseResponse<GetReviewRes>
     */
    @ResponseBody
    @GetMapping("/{restaurantIdx}/reviews/{userIdx}")// (GET) 127.0.0.1:9090/app/restaurants/:restaurantIdx/reviews/:userIdx
    public BaseResponse<GetReviewsRes> getReviews(@PathVariable("restaurantIdx") int restaurantIdx, @PathVariable("userIdx") int userIdx, @RequestParam(required = false) String reviewImgStatus, @RequestParam(required = false) String latestSorting){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용
            if(reviewImgStatus == null && latestSorting == null){  // 사진 리뷰를 보겠다는 값과 최신순 정렬 값 모두 전달 받지 못하는 경우
                GetReviewsRes getReviewsRes = restaurantProvider.getReviews(restaurantIdx);
                return new BaseResponse<>(getReviewsRes);
            } else if (reviewImgStatus != null && latestSorting == null) { // 사진 리뷰만 보겠다는 값만 전달 받고 최신순 정렬 값은 전달 받지 못한 경우
                GetReviewsRes getReviewsRes = restaurantProvider.getReviewsByImg(restaurantIdx, reviewImgStatus);
                return new BaseResponse<>(getReviewsRes);
            } else if (latestSorting == null && latestSorting != null) { // 사진 리뷰만 보겠다는 값은 전달 받지 못하고 최신순 정렬 값만 전달 받은 경우
                GetReviewsRes getReviewsRes = restaurantProvider.getReviewsBySorting(restaurantIdx);
                return new BaseResponse<>(getReviewsRes);
            }GetReviewsRes getReviewsRes = restaurantProvider.getReviewsByImgAndSorting(restaurantIdx, reviewImgStatus); // 모든 값을 전달 받은 경우
            return new BaseResponse<>(getReviewsRes);
        }catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 식당 리뷰 등록 API
     * [GET] /restaurants/:restaurantIdx/reviews/:userIdx
     * @return BaseResponse<GetReviewRes>
     */
    @ResponseBody
    @PostMapping("/{restaurantIdx}/reviews/{userIdx}") // (GET) 127.0.0.1:9090/app/restaurants/:restaurantIdx/reviews/:userIdx
    public BaseResponse<PostReviewRes> createReview(@PathVariable("restaurantIdx") int restaurantIdx, @PathVariable("userIdx") int userIdx, @RequestBody PostReviewReq postReviewReq){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용

            PostReviewRes postReviewRes = restaurantService.createReview(postReviewReq);
            return new BaseResponse<>(postReviewRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 식당 리뷰 수정 API
     * [GET] /restaurants/:userIdx/reviews/:reviewIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/carts/{cartIdx}") // (PATCH) 127.0.0.1:9090/app/restaurants/:userIdx/reviews/:reviewIdx
    public BaseResponse<String> modifyReview(@PathVariable("userIdx") int userIdx, @PathVariable("reviewIdx") int reviewIdx, @RequestBody Review review){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용

            PatchReviewReq patchReviewReq = new PatchReviewReq(reviewIdx, review.getReviewImgUrlOne(), review.getReviewImgStatus(), review.getReviewContents(), review.getReviewStar());
            restaurantService.modifyReview(patchReviewReq);

            String result = "";
            return new BaseResponse<>(result);

        } catch (BaseException exception){
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 식당 리뷰 삭제 API
     * [GET] /restaurants/:userIdx/reviews/:reviewIdx/status
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/reviews/{reviewIdx}/status") // (PATCH) 127.0.0.1:9090/app/restaurants/:userIdx/reviews/:reviewIdx/status
    public BaseResponse<String> modifyReviewStatus(@PathVariable("userIdx") int userIdx, @PathVariable("reviewIdx") int reviewIdx, @RequestBody ReviewStatus reviewStatus){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용

            PatchReviewStatusReq patchReviewStatusReq = new PatchReviewStatusReq(reviewIdx, reviewStatus.getStatus());
            restaurantService.modifyReview(patchReviewStatusReq);

            String result = "";
            return new BaseResponse<>(result);

        } catch (BaseException exception){
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }




    /**
     * 카테고리 조회 API
     * [GET] /categories
     * @return BaseResponse<GetCategoryRes>
     */
    @ResponseBody
    @GetMapping("/categories/{userIdx}")// (GET) 127.0.0.1:9090/app/restaurants/categories/:userIdx
    public BaseResponse<List<GetCategoryRes>> getCategory(@PathVariable("userIdx") int userIdx) {
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용
            List<GetCategoryRes> getCategoryRes = restaurantProvider.getCategory();
            return new BaseResponse<>(getCategoryRes);
        } catch (BaseException exception) {
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
