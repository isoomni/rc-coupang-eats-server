package com.example.demo.src.favorite;

import com.example.demo.config.BaseException;
import com.example.demo.src.favorite.model.*;
import com.example.demo.src.order.OrderDao;
import com.example.demo.src.restaurant.model.GetRestaurantRes;
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
public class FavoriteProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FavoriteDao favoriteDao;

    @Autowired
    public FavoriteProvider(FavoriteDao favoriteDao){
        this.favoriteDao = favoriteDao;
    }


    /**
     * 즐겨찾기 조회
     * [GET] /favorites/:userIdx
     * @return BaseResponse<GetFavoriteRes>
     */
    // 최근 주문한 순
    public GetFavoriteRes getFavoriteByOrder(int favoritesIdx) throws BaseException{
        try{
            GetFavoriteRes getFavoriteRes = favoriteDao.getFavoriteByOrder(favoritesIdx);
            return getFavoriteRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 최근 추가한 순
    public GetFavoriteRes getFavoriteByAdd(int favoritesIdx) throws BaseException{
        try{
            GetFavoriteRes getFavoriteRes = favoriteDao.getFavoriteByAdd(favoritesIdx);
            return getFavoriteRes;
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
