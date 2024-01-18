package com.laioffer.twitch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterBody( //用户注册所用的, 把前端的json转换成这个class
        String username,
        String password,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName
) {
}
