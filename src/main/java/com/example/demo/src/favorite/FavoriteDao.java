package com.example.demo.src.favorite;

import com.example.demo.src.favorite.model.*;
import com.example.demo.src.order.model.PatchOrderStatusReq;
import com.example.demo.src.order.model.PostOrderReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class FavoriteDao {
    private JdbcTemplate jdbcTemplate;
    private GetFavoriteRes getFavoriteRes;
    private GetFavoriteRes1 getFavoriteRes1;
    private List<GetFavoriteRes2> getFavoriteRes2;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    // 최근 주문한 순
    public GetFavoriteRes getFavoriteByOrder(int userIdx){
        // 즐겨찾기 식당 개수
        String Query1 = "SELECT CONCAT('총 ', COUNT(F.restaurantIdx), '개') AS favoriteCount\n" +
                "FROM RC_coupang_eats_d_Riley.Favorites F\n" +
                "WHERE F.userIdx = ?\n" +
                "GROUP BY F.userIdx;";

        // 즐겨찾기 식당
        String Query2 = "SELECT DISTINCT R.subProfileImgOne, R.restaurantName,\n" +
                "CASE\n" +
                "WHEN round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "    -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1) <= 10\n" +
                "THEN (concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "        -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km · ', \n" +
                "    R.deliveryTime,'-',(R.deliveryTime+10),'분 · 배달비 ',concat(format(R.deliveryFee, 0)), '원'))\n" +
                "ELSE ('현재 위치에서 주문 불가')\n" +
                "end as resInfo\n" +
                "FROM RC_coupang_eats_d_Riley.Cart C\n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.Favorites F on C.restaurantIdx = F.restaurantIdx\n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R on F.restaurantIdx = R.restaurantIdx\n" +
                "LEFT JOIN RC_coupang_eats_d_Riley.DeliveryAddress DA on F.userIdx = DA.userIdx\n" +
                "where F.userIdx = ? and DA.status = 'Y'\n" +
                "ORDER BY C.createdAt;";

        int getFavoriteParams1 = userIdx;

        return new GetFavoriteRes(
                getFavoriteRes1 = this.jdbcTemplate.queryForObject(Query1,
                        (rs, rowNum)-> new GetFavoriteRes1(
                                rs.getString("favoriteCount")
                        ), getFavoriteParams1
                ),
                getFavoriteRes2 = this.jdbcTemplate.query(Query2,
                        (rs, rowNum)-> new GetFavoriteRes2(
                                rs.getString("subProfileImgOne"),
                                rs.getString("restaurantName"),
                                rs.getString("resInfo")
                        ), getFavoriteParams1
                ));
    }

    // 최근 추가한 순
    public GetFavoriteRes getFavoriteByAdd(int userIdx){
        //
        String Query1 = "SELECT CONCAT('총 ', COUNT(F.restaurantIdx), '개') AS favoriteCount\n" +
                "FROM RC_coupang_eats_d_Riley.Favorites F\n" +
                "WHERE F.userIdx = ?\n" +
                "GROUP BY F.userIdx;";

        //
        String Query2 = "SELECT R.subProfileImgOne, R.restaurantName,\n" +
                "       CASE\n" +
                "           WHEN round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "               -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1) <= 10\n" +
                "               THEN (concat(round(6371*acos(cos(radians(DA.userLatitude))*cos(radians(R.resLatitude))*cos(radians(R.resLongtitude)\n" +
                "               -radians(R.resLongtitude))+sin(radians(DA.userLatitude))*sin(radians(R.resLatitude))), 1), 'km · ', \n" +
                "                   R.deliveryTime,'-',(R.deliveryTime+10),'분 · 배달비 ',concat(format(R.deliveryFee, 0)), '원'))\n" +
                "           ELSE ('현재 위치에서 주문 불가')\n" +
                "           end as resInfo\n" +
                "FROM RC_coupang_eats_d_Riley.Favorites F\n" +
                "         LEFT JOIN RC_coupang_eats_d_Riley.Restaurant R on F.restaurantIdx = R.restaurantIdx\n" +
                "         LEFT JOIN RC_coupang_eats_d_Riley.DeliveryAddress DA on F.userIdx = DA.userIdx\n" +
                "where F.userIdx = ? and DA.status = 'Y'\n" +
                "ORDER BY F.createdAt;";

        int getFavoriteParams1 = userIdx;

        return new GetFavoriteRes(
                getFavoriteRes1 = this.jdbcTemplate.queryForObject(Query1,
                        (rs, rowNum)-> new GetFavoriteRes1(
                                rs.getString("favoriteCount")
                        ), getFavoriteParams1
                ),
                getFavoriteRes2 = this.jdbcTemplate.query(Query2,
                        (rs, rowNum)-> new GetFavoriteRes2(
                                rs.getString("subProfileImgOne"),
                                rs.getString("restaurantName"),
                                rs.getString("resInfo")
                        ), getFavoriteParams1
                ));
    }

    /**
     * 즐겨찾기 등록
     * [GET] /favorites/:userIdx
     * @return BaseResponse<GetFavoriteRes>
     */
    public int createFavorite(PostFavoriteReq postFavoriteReq){
        String Query1 = "insert into RC_coupang_eats_d_Riley.Favorites (userIdx, favoritesIdx) VALUES (?,?)";
        Object[] createOrderParams = new Object[]{postFavoriteReq.getUserIdx(), postFavoriteReq.getFavoritesIdx()};
        this.jdbcTemplate.update(Query1, createOrderParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    /**
     * 즐겨찾기 삭제
     * [GET] /favorites/:userIdx/:restaurantsIdx/status
     * @return BaseResponse<GetFavoriteRes>
     */
    public int modifyFavorite(PatchFavoriteReq patchFavoriteReq){
        String modifyOrderQuery = "update RC_coupang_eats_d_Riley.Favorites set status = ? where favoritesIdx = ?";
        Object[] modifyOrderParams = new Object[]{patchFavoriteReq.getStatus(), patchFavoriteReq.getFavoritesIdx()};

        return this.jdbcTemplate.update(modifyOrderQuery,modifyOrderParams);
    }


}
