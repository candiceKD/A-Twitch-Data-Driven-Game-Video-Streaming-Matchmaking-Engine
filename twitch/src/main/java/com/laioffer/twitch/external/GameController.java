package com.laioffer.twitch.external;

import com.laioffer.twitch.external.model.Game;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameController {
    private final TwitchService twitchService;

    public GameController(TwitchService twitchService) { //这个constructor是springboot在运行的时候创建的, 是给springboot来用的

        this.twitchService = twitchService;
    }


    @GetMapping("/game")
    public List<Game> getGames(@RequestParam(value = "game_name", required = false) String gameName) {
        if (gameName == null) {
            return twitchService.getTopGames();
        } else {
            return twitchService.getGames(gameName);
        }
    }

}
