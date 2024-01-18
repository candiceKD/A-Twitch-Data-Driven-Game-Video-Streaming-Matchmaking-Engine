package com.laioffer.twitch.db;

import com.laioffer.twitch.db.entity.FavoriteRecordEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface FavoriteRecordRepository extends ListCrudRepository<FavoriteRecordEntity, Long> {


    List<FavoriteRecordEntity> findAllByUserId(Long userId); //db里面column叫什么名字,这里method的名字也要对应


    boolean existsByUserIdAndItemId(Long userId, Long itemId); //在db种column的名字都是snake_case,这里JDBC都会帮你自动转换的

    @Query("SELECT item_id FROM favorite_records WHERE user_id = :userId")
//  :userId是一个place holder,有点类似于url种的?, 后面加java method里面的args的变量名
    List<Long> findFavoriteItemIdsByUserId(Long userId);

    @Modifying    // 告诉JDBC这是一个写操作, 因为SQL里面主要是读操作, 所以要加一个annotation
    @Query("DELETE FROM favorite_records WHERE user_id = :userId AND item_id = :itemId") //两个参数之前用AND连接
    void delete(Long userId, Long itemId);
}

