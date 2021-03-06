package com.example.demo.src.user;

import com.example.demo.src.order.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController
@RequestMapping("/app/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;




    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 회원 조회 API
     * [GET] /users
     * 회원 번호 및 이메일 검색 조회 API
     * [GET] /users? Email=
     * @return BaseResponse<List<GetUserRes>>
     */
    //Query String
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<GetUsersRes>> getUsers(@RequestParam(required = false) String emailAddress) {
        try{
            if(emailAddress == null){
                List<GetUsersRes> getUsersRes = userProvider.getUsers();
                return new BaseResponse<>(getUsersRes);
            }
            // Get Users
            List<GetUsersRes> getUsersRes = userProvider.getUsersByEmail(emailAddress);
            return new BaseResponse<>(getUsersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원 1명 조회 API
     * [GET] /users/:userIdx
     * @return BaseResponse<GetUserRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx) {
        // Get Users
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용
            //같다면 유저네임 변경
            GetUserRes getUsersRes = userProvider.getUser(userIdx);
            return new BaseResponse<>(getUsersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        if(postUserReq.getEmailAddress() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if(postUserReq.getUserName() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_USERNAME);
        }
        if(postUserReq.getPhoneNum() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUMBER);
        }
        if(postUserReq.getPassword() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        if(postUserReq.getPassword() == postUserReq.getEmailAddress()){ // 아이디와 비밀번호가 같습니다.
            return new BaseResponse<>(POST_USERS_PASSWORD_SAME_WITH_EMAIL);
        }

        //이메일 정규표현
        if(!isRegexEmail(postUserReq.getEmailAddress())){  // 정규표현식과 다른 형식으로 받으면 invalid
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        //비밀번호 정규표현
        if (!isRegexPassword(postUserReq.getPassword())){  // 특수문자 / 문자 / 숫자 포함 형태의 8~20자리 이내의 암호 정규식
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        //전화번호 정규표현
        if (!isRegexPhoneNum(postUserReq.getPhoneNum())){  // 01로 시작, 3 + 8 숫자
            return new BaseResponse<>(POST_USERS_INVALID_PHONENUMBER);
        }

        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        try{
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            if(postLoginReq.getEmailAddress() == null){
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            if(postLoginReq.getPassword() == null){
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }
            if(postLoginReq.getPassword() == postLoginReq.getEmailAddress()){ // 아이디와 비밀번호가 같습니다.
                return new BaseResponse<>(POST_USERS_PASSWORD_SAME_WITH_EMAIL);
            }

            //이메일 정규표현
            if(!isRegexEmail(postLoginReq.getEmailAddress())){  // 정규표현식과 다른 형식으로 받으면 invalid
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            //비밀번호 정규표현
            if (!isRegexPassword(postLoginReq.getPassword())){  // 특수문자 / 문자 / 숫자 포함 형태의 8~20자리 이내의 암호 정규식
                return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
            }
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저정보변경 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}")
    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody User user){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용
            //같다면 유저네임 변경
            PatchUserReq patchUserReq = new PatchUserReq(userIdx,user.getUserName());
            userService.modifyUserName(patchUserReq);

            String result = "";
        return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저 탈퇴 API
     * [PATCH] /users/:userIdx/status
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/status") // (PATCH) 127.0.0.1:9090/app/users/:userIdx/status
    public BaseResponse<String> modifyUserStatus(@PathVariable("userIdx") int userIdx, @RequestBody UserStatus userStatus){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용

            PatchUserStatusReq patchUserStatusReq = new PatchUserStatusReq(userIdx, userStatus.getStatus());
            userService.modifyUserStatus(patchUserStatusReq);

            String result = "";
            return new BaseResponse<>(result);

        } catch (BaseException exception){
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 배달 주소 조회 API
     * [GET] /users/:userIdx/addresses
     * @return BaseResponse<GetAddressRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/addresses") // (GET) 127.0.0.1:9090/app/users/:userIdx/addresses
    public BaseResponse<List<GetAddressRes>> getAddress(@PathVariable("userIdx") int userIdx){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용
            //같다면 유저네임 변경
            List<GetAddressRes> getAddressRes = userProvider.getAddress(userIdx);
            return new BaseResponse<>(getAddressRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 배달 주소 등록 API
     * [POST] /users/:userIdx/addresses
     * @return BaseResponse<PostAddressRes>
     */
    @ResponseBody
    @PostMapping("/{userIdx}/addresses") // (GET) 127.0.0.1:9090/app/users/:userIdx/addresses
    public BaseResponse<PostAddressRes> createAddress(@PathVariable("userIdx") int userIdx, @RequestBody PostAddressReq postAddressReq){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용

            if(postAddressReq.getAddressTitle() == null){ // 배달주소 제목을 입력해주세요
                return new BaseResponse<>(POST_ADDRESSES_EMPTY_TITLE);
            }
            if(postAddressReq.getRoadNameAddress() == null){ // 도로명주소를 입력해주세요
                return new BaseResponse<>(POST_ADDRESSES_EMPTY_ROADNAME);
            }
            if(postAddressReq.getDetailedAddress() == null){ // 상세 주소를 입력해주세요
                return new BaseResponse<>(POST_ADDRESSES_EMPTY_DETAIL);
            }
            if(postAddressReq.getUserLatitude() == null){ // 위도가 없습니다.
                return new BaseResponse<>(POST_ADDRESSES_EMPTY_LATITUDE);
            }
            if(postAddressReq.getUserLongtitude() == null){ // 경도가 없습니다.
                return new BaseResponse<>(POST_ADDRESSES_EMPTY_LONGTITUDE);
            }



            PostAddressRes postAddressRes = userService.createAddress(postAddressReq);
            return new BaseResponse<>(postAddressRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 배달 주소 수정 API
     * [PATCH] /users/:userIdx/addresses/:deliveryAddressIdx
     * @return BaseResponse<GetAddressRes>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/addresses/{deliveryAddressIdx}") // (PATCH) 127.0.0.1:9090/app/users/:userIdx/addresses/:deliveryAddressIdx
    public BaseResponse<String> modifyOrder(@PathVariable("userIdx") int userIdx, @PathVariable("deliveryAddressIdx") int deliveryAddressIdx, @RequestBody Address address){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용

            PatchAddressReq patchAddressReq = new PatchAddressReq(deliveryAddressIdx, address.getAddressTitle(), address.getRoadNameAddress(), address.getDetailedAddress(), address.getUserLatitude(), address.getUserLongtitude());
            userService.modifyAddress(patchAddressReq);

            String result = "";
            return new BaseResponse<>(result);

        } catch (BaseException exception){
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 배달 주소 삭제 API
     * [PATCH] /users/:userIdx/addresses/:deliveryAddressIdx/status
     * @return BaseResponse<GetAddressRes>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/addresses/{deliveryAddressIdx}/status") // (PATCH) 127.0.0.1:9000/app/users/:userIdx/addresses/:deliveryAddressIdx/status
    public BaseResponse<String> modifyAddressStatus(@PathVariable("userIdx") int userIdx, @PathVariable("deliveryAddressIdx") int deliveryAddressIdx, @RequestBody AddressStatus addressStatus){
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }  // 이 부분까지는 유저가 사용하는 기능 중 유저에 대한 보안이 철저히 필요한 api 에서 사용

            PatchAddressStatusReq patchAddressStatusReq = new PatchAddressStatusReq(deliveryAddressIdx, addressStatus.getStatus());
            userService.modifyAddressStatus(patchAddressStatusReq);

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
