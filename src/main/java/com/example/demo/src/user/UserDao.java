package com.example.demo.src.user;


import com.example.demo.src.home.model.GetCouponBannerImgListRes;
import com.example.demo.src.order.model.*;
import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;
    private GetUserRes1 getUserRes1;
    private GetCouponBannerImgListRes getCouponBannerImgListRes;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUsersRes> getUsers(){
        String getUsersQuery = "select * from RC_coupang_eats_d_Riley.User";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetUsersRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("emailAddress"),
                        rs.getString("realPassWordForMe"))
                );
    }

    public List<GetUsersRes> getUsersByEmail(String emailAddress){
        String getUsersByEmailQuery = "select * from RC_coupang_eats_d_Riley.User where emailAddress =?";
        String getUsersByEmailParams = emailAddress;
        return this.jdbcTemplate.query(getUsersByEmailQuery,
                (rs, rowNum) -> new GetUsersRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("emailAddress"),
                        rs.getString("realPassWordForMe")),
                getUsersByEmailParams);
    }

    public GetUserRes getUser(int userIdx){
        // 유저 정보
        String getUserQuery = "select U.userName, replace(U.phoneNum, SUBSTRING(U.phoneNum,4,4), '-****-') as phoneNum\n" +
                "from RC_coupang_eats_d_Riley.User U where U.userIdx = ?";
        // 쿠팡잇츠 쿠폰 배너
        String getUserQuery2 = "SELECT CBIU.couponBannerImgUrl FROM RC_coupang_eats_d_Riley.CouponBannerImgUrl CBIU";

        int getUserParams = userIdx;
        return new GetUserRes(
                getUserRes1 = this.jdbcTemplate.queryForObject(getUserQuery,
                        (rs, rowNum) -> new GetUserRes1(
                                rs.getString("userName"),
                                rs.getString("phoneNum")),
                        getUserParams),
                getCouponBannerImgListRes = this.jdbcTemplate.queryForObject(getUserQuery2,
                        (rs, rowNum) -> new GetCouponBannerImgListRes(
                                rs.getString("couponBannerImgUrl")
                        )
                ));
    }
    

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into RC_coupang_eats_d_Riley.User (userIdx, emailAddress, password, userName, phoneNum) VALUES (?,?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getUserIdx(),postUserReq.getEmailAddress(), postUserReq.getPassword(),postUserReq.getUserName(),postUserReq.getPhoneNum()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checkEmail(String emailAddress){
        String checkEmailQuery = "select exists(select emailAddress from RC_coupang_eats_d_Riley.User where emailAddress = ?)";
        String checkEmailParams = emailAddress;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update RC_coupang_eats_d_Riley.User set userName = ? where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getUserName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }

    public User getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "select userIdx, password,emailAddress,userName from RC_coupang_eats_d_Riley.User where emailAddress = ?";
        String getPwdParams = postLoginReq.getEmailAddress();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("password"),
                        rs.getString("emailAddress")
                ),
                getPwdParams
                );

    }

    /**
     * 유저 탈퇴 API
     * [PATCH] /users/:userIdx/status
     * @return BaseResponse<String>
     */
    public int modifyUserStatus(PatchUserStatusReq patchUserStatusReq){
        String modifyOrderQuery = "update RC_coupang_eats_d_Riley.User set status = ? where userIdx = ?";
        Object[] modifyOrderParams = new Object[]{patchUserStatusReq.getStatus(), patchUserStatusReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyOrderQuery,modifyOrderParams);
    }

    /**
     * 배달 주소 조회 API
     * [GET] /users/:userIdx/addresses
     * @return BaseResponse<GetAddressRes>
     */
    public List<GetAddressRes> getAddress(int userIdx){
        // 주소 정보
        String Query1 = "SELECT DA.addressTitle, CONCAT(DA.roadNameAddress, DA.detailedAddress) AS address\n" +
                "FROM RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "WHERE DA.userIdx = ?\n" +
                "ORDER BY FIELD(DA.addressTitle, '회사', '집') DESC, DA.addressTitle ASC;";

        // 파라미터
        int getOrderParams1 = userIdx;

        // 리턴
        return this.jdbcTemplate.query(Query1,
                        (rs, rowNum)->new GetAddressRes(
                                rs.getString("addressTitle"),
                                rs.getString("address")
                        ),getOrderParams1
        );
    }

    /**
     * 배달 주소 등록 API
     * [GET] /users/:userIdx/addresses
     * @return BaseResponse<GetAddressRes>
     */
    public int createAddress(PostAddressReq postAddressReq){
        String Query1 = "insert into RC_coupang_eats_d_Riley.DeliveryAddress (userIdx, addressTitle, roadNameAddress, detailedAddress, userLatitude, userLongtitude) VALUES (?,?,?,?,?,?)";
        Object[] createOrderParams = new Object[]{postAddressReq.getUserIdx(), postAddressReq.getAddressTitle(), postAddressReq.getRoadNameAddress(), postAddressReq.getDetailedAddress(), postAddressReq.getUserLatitude(), postAddressReq.getUserLongtitude()};
        this.jdbcTemplate.update(Query1, createOrderParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }
    /**
     * 배달 주소 수정 API
     * [GET] /users/:userIdx/addresses/:deliveryAddressIdx
     * @return BaseResponse<GetAddressRes>
     */
    public int modifyAddress(PatchAddressReq patchAddressReq){
        String modifyOrderQuery = "update RC_coupang_eats_d_Riley.DeliveryAddress set addressTitle = ?, roadNameAddress = ?, detailedAddress = ?, userLatitude = ?, userLongtitude = ? where deliveryAddressIdx = ?";
        Object[] modifyOrderParams = new Object[]{patchAddressReq.getAddressTitle(), patchAddressReq.getRoadNameAddress(), patchAddressReq.getDetailedAddress(), patchAddressReq.getUserLatitude(), patchAddressReq.getUserLongtitude(), patchAddressReq.getDeliveryAddressIdx()};

        return this.jdbcTemplate.update(modifyOrderQuery,modifyOrderParams);
    }

    /**
     * 배달 주소 삭제 API
     * [GET] /users/:userIdx/addresses/:deliveryAddressIdx/status
     * @return BaseResponse<GetAddressRes>
     */
    public int modifyAddressStatus(PatchAddressStatusReq patchAddressStatusReq){
        String modifyOrderQuery = "update RC_coupang_eats_d_Riley.DeliveryAddress set status = ? where deliveryAddressIdx = ?";
        Object[] modifyOrderParams = new Object[]{patchAddressStatusReq.getStatus(), patchAddressStatusReq.getDeliveryAddressIdx()};

        return this.jdbcTemplate.update(modifyOrderQuery,modifyOrderParams);
    }



}
