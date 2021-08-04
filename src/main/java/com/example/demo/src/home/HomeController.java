package com.example.demo.src.home;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.home.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/app/homes")
public class HomeController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final HomeProvider homeProvider;
    @Autowired
    private final HomeService homeService;

    public HomeController(HomeProvider homeProvider, HomeService homeService){
        this.homeProvider = homeProvider;
        this.homeService = homeService;
    }

    /**
     * 홈 화면 조회 API
     * [GET] /homes
     * @return BaseResponse<List<GetHomeRes>>
     */
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/homes
    public BaseResponse<GetHomeRes> getHome(@RequestParam(required = false) String chitaDeliveryStatus){
        // Get Users
        try{
            if(chitaDeliveryStatus == null){ // city 값이 없는 경우에는
                GetHomeRes getHomeRes = homeProvider.getHome(); //전체 숙소를 조회하게 했고
                return new BaseResponse<>(getHomeRes);
            }
            GetHomeRes getHomeRes = homeProvider.getHomeByFilter(chitaDeliveryStatus); // city 값이 있는 경우에는 email 로 필터링 된 숙소를 조회하게 했다.
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
