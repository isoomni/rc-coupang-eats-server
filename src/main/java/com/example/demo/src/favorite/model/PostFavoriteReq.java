package com.example.demo.src.favorite.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class PostFavoriteReq {
    private int userIdx;
    private int favoritesIdx;
}
