package com.laioffer.twitch.user;

import com.laioffer.twitch.model.RegisterBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {


    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register") //只提供了一个API, login和logout都直接使用springboot提供的现成的
    @ResponseStatus(value = HttpStatus.OK)
    //写或不写都是可以的, 如果你是想不管是成功还有别的意义, 这里还有一些别的status code
    public void register(@RequestBody RegisterBody body) {
        userService.register(body.username(), body.password(), body.firstName(), body.lastName());
    }
}
//login和logout都直接用spring提供的
