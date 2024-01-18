package com.laioffer.twitch.model;

import com.laioffer.twitch.db.entity.ItemEntity;

public record FavoriteRequestBody( //从前端发来的请求, 在这里做一个转换,把Json转换为一个java class
        ItemEntity favorite
                                   //前端发来请求的body里面是要处理的一个favorite 的item的json string的内容, 在这里转换为这个record
                                   //这个转换是spring通过Jackson帮我们自动转换的
) {}
