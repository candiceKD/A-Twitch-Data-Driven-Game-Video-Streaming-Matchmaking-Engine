package com.laioffer.twitch.external;


import com.laioffer.twitch.external.model.Clip;
import com.laioffer.twitch.external.model.Game;
import com.laioffer.twitch.external.model.Stream;
import com.laioffer.twitch.external.model.Video;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;


@Service
public class TwitchService {


    private final TwitchApiClient twitchApiClient;


    public TwitchService(TwitchApiClient twitchApiClient) {
        this.twitchApiClient = twitchApiClient;
    }

//这两个cache只是为了读数据,没有任何的其他写操作, 一分钟后expired,就再去重新拿数据
    @Cacheable("top_games") //当这个API被call的时候去叫做top_games的cache空间去找看看有没有缓存版,如果有缓存版本之前用缓存版
    public List<Game> getTopGames() {
        return twitchApiClient.getTopGames().data();
    }


    @Cacheable("games_by_name")//这个cache比hashmap好处是有TTL,还有其他各种好处
    //因为这里面传入一个参数,所以cache是根据名字来的
    public List<Game> getGames(String name) {
        return twitchApiClient.getGames(name).data();
    }


    public List<Stream> getStreams(List<String> gameIds, int first) {
        return twitchApiClient.getStreams(gameIds, first).data();
    }


    public List<Video> getVideos(String gameId, int first) {
        return twitchApiClient.getVideos(gameId, first).data();
    }


    public List<Clip> getClips(String gameId, int first) {
        return twitchApiClient.getClips(gameId, first).data();
    }


    public List<String> getTopGameIds() {
        List<String> topGameIds = new ArrayList<>();
        for (Game game : getTopGames()) {
            topGameIds.add(game.id());
        }
        return topGameIds;
    }
}
