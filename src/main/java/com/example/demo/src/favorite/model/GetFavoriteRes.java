package com.example.demo.src.favorite.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetFavoriteRes {
    private GetFavoriteRes1 getFavoriteRes1;
    private List<GetFavoriteRes2> getFavoriteRes2;
}
