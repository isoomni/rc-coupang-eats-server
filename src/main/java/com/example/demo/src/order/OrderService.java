package com.example.demo.src.order;

import com.example.demo.config.BaseException;
import com.example.demo.src.order.model.*;
import com.example.demo.src.restaurant.RestaurantDao;
import com.example.demo.src.restaurant.RestaurantProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.example.demo.config.BaseResponseStatus.*;

// Service : Create, Update, Delete 의 로직 처리
@Service
public class OrderService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OrderDao orderDao;;
    private final OrderProvider orderProvider;

    @Autowired
    public OrderService(OrderDao orderDao, OrderProvider orderProvider){
        this.orderDao = orderDao;
        this.orderProvider = orderProvider;
    }

    /**
     * 카트 등록 API
     * [POST] /orders/:userIdx/carts
     * @return BaseResponse<GetOrderRes>
     */
    public PostOrderRes createOrder(PostOrderReq postOrderReq) throws BaseException { // 숙소정보를 생성하려고 하는데

        try {
            int cartIdx = orderDao.createOrder(postOrderReq);
            return new PostOrderRes(cartIdx);
        } catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }
    /**
     * 카트 수정 API
     * [PATCH] /orders/:userIdx/carts/:cartIdx
     * @return BaseResponse<String>
     */
    public void modifyOrder(PatchOrderReq patchOrderReq) throws BaseException {
        try{
            int result = orderDao.modifyOrder(patchOrderReq);
            if (result == 0){
                throw new BaseException(MODIFY_FAIL_ORDER);
            }
        } catch(Exception exception){
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /**
     * 카트 삭제 API
     * [PATCH] /orders/:userIdx/carts/:cartIdx/status
     * @return BaseResponse<String>
     */
    public void modifyOrderStatus(PatchOrderStatusReq patchOrderStatusReq) throws BaseException {
        try{
            int result = orderDao.modifyOrderStatus(patchOrderStatusReq);
            if (result == 0){
                throw new BaseException(MODIFY_FAIL_ORDER);
            }
        } catch(Exception exception){
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
