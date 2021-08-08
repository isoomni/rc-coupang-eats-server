package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUserRes> getUsers(){
        String getUsersQuery = "select * from RC_coupang_eats_d_Riley.User";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("emailAddress"),
                        rs.getString("realPassWordForMe"))
                );
    }

    public List<GetUserRes> getUsersByEmail(String emailAddress){
        String getUsersByEmailQuery = "select * from RC_coupang_eats_d_Riley.User where emailAddress =?";
        String getUsersByEmailParams = emailAddress;
        return this.jdbcTemplate.query(getUsersByEmailQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("emailAddress"),
                        rs.getString("realPassWordForMe")),
                getUsersByEmailParams);
    }

    public GetUserRes getUser(int userIdx){
        String getUserQuery = "select * from RC_coupang_eats_d_Riley.User where userIdx = ?";
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("emailAddress"),
                        rs.getString("realPassWordForMe")),
                getUserParams);
    }
    

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into RC_coupang_eats_d_Riley.User (userIdx, emailAddress, password, userName, phoneNum) VALUES (?,?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getUserIdx(), postUserReq.getEmailAddress(), postUserReq.getPassword(),postUserReq.getUserName(),postUserReq.getPhoneNum()};
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



}
