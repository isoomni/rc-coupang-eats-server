package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUsersRes {
    private int userIdx;
    private String userName;
    private String emailAddress;
    private String realPassWordForMe;
}
