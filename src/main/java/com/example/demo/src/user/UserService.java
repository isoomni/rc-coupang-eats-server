package com.example.demo.src.user;



import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.order.model.PatchOrderReq;
import com.example.demo.src.order.model.PatchOrderStatusReq;
import com.example.demo.src.order.model.PostOrderReq;
import com.example.demo.src.order.model.PostOrderRes;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    //POST
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        //중복
        if(userProvider.checkEmail(postUserReq.getEmailAddress()) ==1){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        String pwd;
        try{
            //암호화
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            int userIdx = userDao.createUser(postUserReq);
            //jwt 발급.
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt,userIdx);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyUserName(PatchUserReq patchUserReq) throws BaseException {
        try{
            int result = userDao.modifyUserName(patchUserReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }
        } catch(Exception exception){
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 유저 탈퇴 API
     * [PATCH] /users/:userIdx/status
     * @return BaseResponse<String>
     */
    public void modifyUserStatus(PatchUserStatusReq patchUserStatusReq) throws BaseException {
        try{
            int result = userDao.modifyUserStatus(patchUserStatusReq);
            if (result == 0){
                throw new BaseException(MODIFY_FAIL_ORDER);
            }
        } catch(Exception exception){
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 배달 주소 등록 API
     * [GET] /users/:userIdx/addresses
     * @return BaseResponse<GetAddressRes>
     */
    public PostAddressRes createAddress(PostAddressReq postAddressReq) throws BaseException { // 숙소정보를 생성하려고 하는데

        try {
            int deliveryAddressIdx = userDao.createAddress(postAddressReq);
            return new PostAddressRes(deliveryAddressIdx);
        } catch (Exception exception){
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }

    /**
     * 배달 주소 수정 API
     * [GET] /users/:userIdx/addresses/:deliveryAddressIdx
     * @return BaseResponse<GetAddressRes>
     */
    public void modifyAddress(PatchAddressReq patchAddressReq) throws BaseException {
        try{
            int result = userDao.modifyAddress(patchAddressReq);
            if (result == 0){
                throw new BaseException(MODIFY_FAIL_ORDER);
            }
        } catch(Exception exception){
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /**
     * 배달 주소 삭제 API
     * [GET] /users/:userIdx/addresses/:deliveryAddressIdx/status
     * @return BaseResponse<GetAddressRes>
     */
    public void modifyAddressStatus(PatchAddressStatusReq patchAddressStatusReq) throws BaseException {
        try{
            int result = userDao.modifyAddressStatus(patchAddressStatusReq);
            if (result == 0){
                throw new BaseException(MODIFY_FAIL_ORDER);
            }
        } catch(Exception exception){
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
