package com.laioffer.twitch.db;

import com.laioffer.twitch.db.entity.UserEntity;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface UserRepository extends ListCrudRepository<UserEntity, Long> {
         //为什么这里要再加一个long作为第二个参数, 第一个参数里面已经详细定义了primary key了?
    //因为当后面如果你调用findItemById这个method, 如果当这个table的primary不是long这个type了,那你就要传入的id是其他的对应的类型
    //就不会让你后面传入参数的时候confused


    List<UserEntity> findByLastName(String lastName);

    List<UserEntity> findByFirstName(String firstName);

    UserEntity findByUsername(String username);

    @Modifying
    @Query("UPDATE users SET first_name = :firstName, last_name = :lastName WHERE username = :username")
        //UPDATE 后面都要用SET关键字
    void updateNameByUsername(String username, String firstName, String lastName);
}
