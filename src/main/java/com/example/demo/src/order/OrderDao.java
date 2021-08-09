package com.example.demo.src.order;

import com.example.demo.src.order.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class OrderDao {

    private JdbcTemplate jdbcTemplate;
    private GetOrderRes getOrderRes;
    private GetOrderRes1 getOrderRes1;
    private GetOrderRes2 getOrderRes2;
    private List<GetOrderRes3> getOrderRes3;
    private GetOrderRes4 getOrderRes4;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 카트 조회 API
     * [GET] /orders/:userIdx/carts/:cartIdx
     * @return BaseResponse<GetOrderRes>
     */
    public GetOrderRes getOrder(int userIdx, int cartIdx){
        // 배달 정보
        String Query1 = "SELECT DA.addressTitle, CONCAT(DA.roadNameAddress, ' ', DA.detailedAddress) as address\n" +
                "FROM RC_coupang_eats_d_Riley.DeliveryAddress DA\n" +
                "WHERE DA.userIdx = ? and DA.status ='Y';";
        // 가게 이름
        String Query2 = "SELECT R.restaurantName\n" +
                "FROM RC_coupang_eats_d_Riley.Cart C LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R ON C.restaurantIdx = R.restaurantIdx\n" +
                "WHERE C.cartIdx = ?;";
        // 주문 메뉴
        String Query3 = "SELECT M.menuName, GROUP_CONCAT(SM.subMenuName) AS subMenuName, CONCAT(FORMAT(IF(SM.subMenuAdditionalAmount != null,\n" +
                "CM.menuQuantity*(M.menuAmount+SM.subMenuAdditionalAmount), CM.menuQuantity*M.menuAmount), 0), '원') as menuAmount, CM.menuQuantity\n" +
                "FROM RC_coupang_eats_d_Riley.Cart C\n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.CartedMenu CM on C.cartIdx = CM.cartIdx\n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.Menu M on CM.menuIdx = M.menuIdx\n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.CartedSubMenu CSM on CM.cartedMenuIdx = CSM.cartedMenuIdx\n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.SubMenu SM on CSM.subMenuIdx = SM.subMenuIdx\n" +
                "WHERE C.cartIdx = ?\n" +
                "GROUP BY CSM.cartedMenuIdx;";
        // 결제 & 요청사항 & 결제수단
        String Query4 = "SELECT IF(C.restaurantIdx = Cp.restaurantIdx,COUNT(UC.couponIdx), '0') as usableCouponCount,\n" +
                "       CONCAT(FORMAT(SUM(IF(SM.subMenuAdditionalAmount != null, CM.menuQuantity*(M.menuAmount+SM.subMenuAdditionalAmount), CM.menuQuantity*M.menuAmount)), 0), '원') as orderAmount,\n" +
                "       CONCAT('+', FORMAT(R.deliveryFee, 0),'원') AS deliveryFee,\n" +
                "       IF(C.restaurantIdx = Cp.restaurantIdx,CONCAT('-', FORMAT(Cp.couponAmount, 0), '원'), '0') as couponAmount,\n" +
                "       CONCAT(FORMAT(SUM(IF(SM.subMenuAdditionalAmount != null, CM.menuQuantity*(M.menuAmount+SM.subMenuAdditionalAmount), CM.menuQuantity*M.menuAmount)) + R.deliveryFee - IF(C.restaurantIdx = Cp.restaurantIdx,Cp.couponAmount, '0'), 0), '원') as totalAmount,\n" +
                "       C.requestedTermToOwner, C.disposableItemReceivingStatus, C.requestedTermToDeliveryMan,C.paymentMethodIdx\n" +
                "FROM RC_coupang_eats_d_Riley.Cart C\n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R on C.restaurantIdx = R.restaurantIdx\n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.User U on C.userIdx = U.userIdx\n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.UserCoupon UC on U.userIdx = UC.userIdx\n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.Coupon Cp on UC.couponIdx = Cp.couponIdx\n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.CartedMenu CM on C.cartIdx = CM.cartIdx\n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.Menu M on CM.menuIdx = M.menuIdx\n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.CartedSubMenu CSM on CM.cartedMenuIdx = CSM.cartedMenuIdx\n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.SubMenu SM on CSM.subMenuIdx = SM.subMenuIdx\n" +
                "WHERE C.cartIdx = ?\n" +
                "GROUP BY Cp.restaurantIdx;";

        // 파라미터
        int getOrderParams1 = userIdx;
        int getOrderParams2 = cartIdx;

        // 리턴
        return new GetOrderRes(
                getOrderRes1 = this.jdbcTemplate.queryForObject(Query1,
                        (rs, rowNum)->new GetOrderRes1(
                                rs.getString("addressTitle"),
                                rs.getString("address")
                        ),getOrderParams1 ),
                getOrderRes2 = this.jdbcTemplate.queryForObject(Query2,
                        (rs, rowNum)->new GetOrderRes2(
                                rs.getString("restaurantName")
                        ), getOrderParams2),
                getOrderRes3 = this.jdbcTemplate.query(Query3,
                        (rs, rowNum)->new GetOrderRes3(
                                rs.getString("menuName"),
                                rs.getString("subMenuName"),
                                rs.getString("menuAmount"),
                                rs.getInt("menuQuantity")
                        ), getOrderParams2),
                getOrderRes4 = this.jdbcTemplate.queryForObject(Query4,
                        (rs, rowNum)->new GetOrderRes4(
                                rs.getString("usableCouponCount"),
                                rs.getString("orderAmount"),
                                rs.getString("deliveryFee"),
                                rs.getString("couponAmount"),
                                rs.getString("totalAmount"),
                                rs.getString("requestedTermToOwner"),
                                rs.getString("disposableItemReceivingStatus"),
                                rs.getString("requestedTermToDeliveryMan"),
                                rs.getInt("paymentMethodIdx")
                        ), getOrderParams2)
        );
    }

}
