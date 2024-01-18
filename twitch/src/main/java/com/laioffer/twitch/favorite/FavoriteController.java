package com.laioffer.twitch.favorite;

import com.laioffer.twitch.db.entity.UserEntity;
import com.laioffer.twitch.model.DuplicateFavoriteException;
import com.laioffer.twitch.model.FavoriteRequestBody;
import com.laioffer.twitch.model.TypeGroupedItemList;
import com.laioffer.twitch.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/favorite")
//如果下面的Mapping request的url是一样的,那么可以统一写在上面
//method不一样, url一样,就做不一样的事情
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserService userService;
    //如果我们不用hard code一个user, 那么我们就要inject userService作为dependency

    // Hard-coded user for temporary use, will be replaced in future
    // private final UserEntity userEntity = new UserEntity(1L, "user0", "Foo", "Bar", "password");
//虚拟一个一号用户,用它来测试favorite的功能, 这只是一个临时的,用来测试的
    //这个userEntity只是被创建出来,但并没有被保存到DB里,是在DevelopmentTester测试的时候被创建的

    public FavoriteController(FavoriteService favoriteService, UserService userService) {
        this.favoriteService = favoriteService;
        this.userService = userService;
    }


    @GetMapping
    public TypeGroupedItemList getFavoriteItems(@AuthenticationPrincipal User user) {
        //@AuthenticationPrincipal User user   springboot在你发请求的时候可以specify当前的用户是谁的
        //我们通过spring security的user detail manager得到user
        UserEntity userEntity = userService.findByUsername(user.getUsername());
        //然后拿到username通过这个function来传入一个userEntity
        return favoriteService.getGroupedFavoriteItems(userEntity);
    }


    @PostMapping
    public void setFavoriteItem(@AuthenticationPrincipal User user, @RequestBody FavoriteRequestBody body) {
        UserEntity userEntity = userService.findByUsername(user.getUsername());
        try {
            favoriteService.setFavoriteItem(userEntity, body.favorite());
        } catch (DuplicateFavoriteException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate entry for favorite record", e);
            //告诉前端你发过来的请求不对,后端不接受这个请求
        }
    }


    @DeleteMapping
    public void unsetFavoriteItem(@AuthenticationPrincipal User user, @RequestBody FavoriteRequestBody body) {
        UserEntity userEntity = userService.findByUsername(user.getUsername());
        favoriteService.unsetFavoriteItem(userEntity, body.favorite().twitchId());
    }
}

