package com.example.demo.src.order.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetOrderRes3 {
    private String menuName;
    private String subMenuName;
    private String menuAmount;
    private int menuQuantity;
}
