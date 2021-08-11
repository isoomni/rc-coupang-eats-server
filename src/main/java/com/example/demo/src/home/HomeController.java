package com.example.demo.src.home;

import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.home.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;


@RestController
@RequestMapping("/app/homes")
public class HomeController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final HomeProvider homeProvider;
    @Autowired
    private final HomeService homeService;
    @Autowired
    private final JwtService jwtService;

    public HomeController(HomeProvider homeProvider, HomeService homeService, JwtService jwtService){
        this.homeProvider = homeProvider;
        this.homeService = homeService;
        this.jwtService = jwtService;
    }

    /**
     * 홈 화면 조회 API
     * [GET] /homes
     * @return BaseResponse<GetHomeRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/{deliveryAddressIdx}") // (GET) 127.0.0.1:9000/app/homes/:userIdx/:deliveryAddressIdx
    public BaseResponse<GetHomeRes> getHome(@PathVariable("userIdx") int userIdx, @PathVariable("deliveryAddressIdx") int deliveryAddressIdx,@RequestParam(required = false) String chitaDeliveryStatus, @RequestParam(required = false) String couponStatus, @RequestParam(required = false) Double minDeliveryAmount){
        // Get Users
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용
            //같다면 유저네임 변경
            if(chitaDeliveryStatus == null && couponStatus == null && minDeliveryAmount == null){ // 치타 배달에 대한 값 혹은 쿠폰에 대한 값이 쿼리 스트링으로 전달 받지 못하는 경우에는
                GetHomeRes getHomeRes = homeProvider.getHome(deliveryAddressIdx); //전체 식당를 조회하고
                return new BaseResponse<>(getHomeRes);
            }else if (chitaDeliveryStatus != null && couponStatus == null && minDeliveryAmount == null){  // 치타 배달 값만 전달된 경우에는
                GetHomeRes getHomeRes = homeProvider.getHomeByChitaFilter(deliveryAddressIdx, chitaDeliveryStatus); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getHomeRes);
            }else if (chitaDeliveryStatus == null && couponStatus != null && minDeliveryAmount == null) { // 쿠폰 값만 전달된 경우에는
                GetHomeRes getHomeRes = homeProvider.getHomeByCouponFilter(deliveryAddressIdx, couponStatus); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getHomeRes);
            }else if (chitaDeliveryStatus == null && couponStatus == null && minDeliveryAmount != null) { // 최소 주문 값만 전달된 경우에는
                GetHomeRes getHomeRes = homeProvider.getHomeByMinDeliveryAmountFilter(deliveryAddressIdx, minDeliveryAmount); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getHomeRes);
            }else if (chitaDeliveryStatus != null && couponStatus == null && minDeliveryAmount != null) { // 치타 배달 값, 최소 주문 값만 전달된 경우에는
                GetHomeRes getHomeRes = homeProvider.getHomeByChitaAndMinFilter(deliveryAddressIdx,chitaDeliveryStatus, minDeliveryAmount); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getHomeRes);
            }else if (chitaDeliveryStatus != null && couponStatus != null && minDeliveryAmount == null) { // 치타 배달 값, 쿠폰 값만 전달된 경우에는
                GetHomeRes getHomeRes = homeProvider.getHomeByChitaAndCouponFilter(deliveryAddressIdx,chitaDeliveryStatus, couponStatus); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getHomeRes);
            }else if (chitaDeliveryStatus == null && couponStatus != null && minDeliveryAmount != null) { // 쿠폰 값, 최소 주문 값만 전달된 경우에는
                GetHomeRes getHomeRes = homeProvider.getHomeByCouponAndMinFilter(deliveryAddressIdx, minDeliveryAmount, couponStatus); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
                return new BaseResponse<>(getHomeRes);

            } GetHomeRes getHomeRes = homeProvider.getHomeByFilter(deliveryAddressIdx, chitaDeliveryStatus, couponStatus, minDeliveryAmount); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
            return new BaseResponse<>(getHomeRes);

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
