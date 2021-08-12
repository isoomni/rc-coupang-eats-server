package com.example.demo.src.favorite;

import com.example.demo.src.order.OrderProvider;
import com.example.demo.src.order.OrderService;
import com.example.demo.src.order.model.Order;
import com.example.demo.src.order.model.PatchOrderStatusReq;
import com.example.demo.src.order.model.PostOrderReq;
import com.example.demo.src.order.model.PostOrderRes;
import com.example.demo.src.restaurant.model.GetReviewsRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.favorite.model.*;
import com.example.demo.utils.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/app/favorites")
public class FavoriteController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final FavoriteProvider favoriteProvider;
    @Autowired
    private final FavoriteService favoriteService;
    @Autowired
    private final JwtService jwtService;

    public FavoriteController(FavoriteProvider favoriteProvider, FavoriteService favoriteService, JwtService jwtService){
        this.favoriteProvider = favoriteProvider;
        this.favoriteService = favoriteService;
        this.jwtService = jwtService;
    }
    /**
     * 즐겨찾기 조회
     * [GET] /favorites/:userIdx
     * @return BaseResponse<GetFavoriteRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}")// (GET) 127.0.0.1:9090/app/favorites/:userIdx
    public BaseResponse<GetFavoriteRes> getFavorite(@PathVariable("userIdx") int userIdx, @RequestParam(required = false) String latestAddSorting, @RequestParam(required = false) String latestOrderSorting){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용

            if(latestAddSorting == null && latestOrderSorting != null) {  // 최근 주문한 순
                GetFavoriteRes getFavoriteRes = favoriteProvider.getFavoriteByOrder(userIdx);
                return new BaseResponse<>(getFavoriteRes);
            } GetFavoriteRes getFavoriteRes = favoriteProvider.getFavoriteByAdd(userIdx); // 최근 추가한 순
                return new BaseResponse<>(getFavoriteRes);
        }catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 즐겨찾기 등록
     * [GET] /favorites/:userIdx
     * @return BaseResponse<GetFavoriteRes>
     */
    @ResponseBody
    @PostMapping("/{userIdx}") // (GET) 127.0.0.1:9090/app/favorites/:userIdx
    public BaseResponse<PostFavoriteRes> createFavorite(@PathVariable("userIdx") int userIdx, @RequestBody PostFavoriteReq postFavoriteReq){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용

            PostFavoriteRes postFavoriteRes = favoriteService.createFavorite(postFavoriteReq);
            return new BaseResponse<>(postFavoriteRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 즐겨찾기 삭제
     * [GET] /favorites/:userIdx/:restaurantsIdx/status
     * @return BaseResponse<GetFavoriteRes>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/{favoritesIdx}/status") // (PATCH) 127.0.0.1:9000/app/favorites/:userIdx/:restaurantsIdx/status
    public BaseResponse<String> modifyOrderStatus(@PathVariable("userIdx") int userIdx,@PathVariable("favoritesIdx") int favoritesIdx, @RequestBody Favorite favorite){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용

            PatchFavoriteReq patchFavoriteReq = new PatchFavoriteReq(favoritesIdx, favorite.getStatus());
            favoriteService.modifyFavorite(patchFavoriteReq);

            String result = "";
            return new BaseResponse<>(result);

        } catch (BaseException exception){
            exception.printStackTrace();
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
