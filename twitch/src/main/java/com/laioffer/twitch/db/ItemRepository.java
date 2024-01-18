package com.laioffer.twitch.db;

import com.laioffer.twitch.db.entity.ItemEntity;
import org.springframework.data.repository.ListCrudRepository;


public interface ItemRepository extends ListCrudRepository<ItemEntity, Long> {
//    extends这个spring里的ListCrudRepository, springboot就知道ItemRepository可以去操纵database, 就可以根据你的API来生成query
    //    括号里specify两个变量,一个是这个ItemRepository读出来的数据应该是什么type, 一个是你的primary key是哪种类型的

    ItemEntity findByTwitchId(String twitchId);
//    根据你的function name来理解你的query怎么实行的
    //    如果你extends的repository已经不足以支持你想要的API了, 那么你可以根据官方documentation自己来定义query
}
