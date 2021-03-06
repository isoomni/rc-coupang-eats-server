package com.example.demo.src.home;

import com.example.demo.config.BaseException;
import com.example.demo.src.home.model.*;
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
public class HomeProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HomeDao homeDao;

    @Autowired
    public HomeProvider(HomeDao homeDao){
        this.homeDao = homeDao;
    }

    // 여기서부터 본문
    public GetHomeRes getHome(int deliveryAddressIdx) throws BaseException{
        try{
            GetHomeRes getHomeRes = homeDao.getHome(deliveryAddressIdx);
            return getHomeRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 치타
    public GetHomeRes getHomeByChitaFilter(int deliveryAddressIdx, String chitaDeliveryStatus) throws BaseException{
        try{
            GetHomeRes getHomeRes = homeDao.getHomeByChitaFilter(deliveryAddressIdx, chitaDeliveryStatus);
            return getHomeRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    //쿠폰
    public GetHomeRes getHomeByCouponFilter(int deliveryAddressIdx, String couponStatus) throws BaseException{
        try{
            GetHomeRes getHomeRes = homeDao.getHomeByCouponFilter(deliveryAddressIdx, couponStatus);
            return getHomeRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 최소 주문액
    public GetHomeRes getHomeByMinDeliveryAmountFilter(int deliveryAddressIdx, Double minDeliveryAmount) throws BaseException{
        try{
            GetHomeRes getHomeRes = homeDao.getHomeByMinDeliveryAmountFilter(deliveryAddressIdx, minDeliveryAmount);
            return getHomeRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 치타 배달 & 최소 주문
    public GetHomeRes getHomeByChitaAndMinFilter(int deliveryAddressIdx, String chitaDeliveryStatus, Double minDeliveryAmount) throws BaseException{
        try{
            GetHomeRes getHomeRes = homeDao.getHomeByChitaAndMinFilter(deliveryAddressIdx, chitaDeliveryStatus, minDeliveryAmount);
            return getHomeRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 치타 배달 & 쿠폰
    public GetHomeRes getHomeByChitaAndCouponFilter(int deliveryAddressIdx, String chitaDeliveryStatus, String couponStatus) throws BaseException{
        try{
            GetHomeRes getHomeRes = homeDao.getHomeByChitaAndCouponFilter(deliveryAddressIdx, chitaDeliveryStatus, couponStatus);
            return getHomeRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 쿠폰 & 최소 주문
    public GetHomeRes getHomeByCouponAndMinFilter(int deliveryAddressIdx, Double minDeliveryAmount, String couponStatus) throws BaseException{
        try{
            GetHomeRes getHomeRes = homeDao.getHomeByCouponAndMinFilter(deliveryAddressIdx, minDeliveryAmount, couponStatus);
            return getHomeRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 모두 다 있는 필터
    public GetHomeRes getHomeByFilter(int deliveryAddressIdx, String chitaDeliveryStatus, String couponStatus, Double minDeliveryAmount) throws BaseException{
        try{
            GetHomeRes getHomeRes = homeDao.getHomeByFilter(deliveryAddressIdx, chitaDeliveryStatus, couponStatus, minDeliveryAmount);
            return getHomeRes;
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
