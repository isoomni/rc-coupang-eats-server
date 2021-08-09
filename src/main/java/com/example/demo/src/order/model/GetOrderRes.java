package com.example.demo.src.order.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetOrderRes {
    private GetOrderRes1 getOrderRes1;
    private GetOrderRes2 getOrderRes2;
    private List<GetOrderRes3> getOrderRes3;
    private GetOrderRes4 getOrderRes4;
}
