package com.laioffer.twitch.recommendation;

import com.laioffer.twitch.db.entity.ItemEntity;
import com.laioffer.twitch.db.entity.UserEntity;
import com.laioffer.twitch.external.TwitchService;
import com.laioffer.twitch.external.model.Clip;
import com.laioffer.twitch.external.model.Stream;
import com.laioffer.twitch.external.model.Video;
import com.laioffer.twitch.favorite.FavoriteService;
import com.laioffer.twitch.model.TypeGroupedItemList;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class RecommendationService {

    private static final int MAX_GAME_SEED = 3;
    private static final int PER_PAGE_ITEM_SIZE = 20;


    private final TwitchService twitchService;
    private final FavoriteService favoriteService;


    public RecommendationService(TwitchService twitchService, FavoriteService favoriteService) {
        this.twitchService = twitchService;
        this.favoriteService = favoriteService;
    }



    @Cacheable("recommend_items")  //specify了cache,它就自动创建了
    public TypeGroupedItemList recommendItems(UserEntity userEntity) {
        List<String> gameIds;
        Set<String> exclusions = new HashSet<>();
        if (userEntity == null) { //当还没有登录的时候, 就给你看最popular的
            gameIds  = twitchService.getTopGameIds();
        } else {
            List<ItemEntity> items = favoriteService.getFavoriteItems(userEntity);
            if (items.isEmpty()) {//当新用户还没有收藏自己的favorite的时候,还是看最popular的
                gameIds = twitchService.getTopGameIds();
            } else {
                Set<String> uniqueGameIds = new HashSet<>();//去重
                for (ItemEntity item : items) {
                    uniqueGameIds.add(item.gameId());
                    exclusions.add(item.twitchId());
                }
                gameIds = new ArrayList<>(uniqueGameIds);
            }
        }


        int gameSize = Math.min(gameIds.size(), MAX_GAME_SEED);
        //当你收藏的gameId数量过多的时候,我们就用Math.min去cap一下, 因为去call twitch的API,过多的数量会造成latency
        int perGameListSize = PER_PAGE_ITEM_SIZE / gameSize;
        //perGameListSize就是PER_PAGE_ITEM_SIZE除以gameIds的个数,平均分配一下个数


        List<ItemEntity> streams = recommendStreams(gameIds, exclusions);
        List<ItemEntity> clips = recommendClips(gameIds.subList(0, gameSize), perGameListSize, exclusions);
                                                       //取一个subList
        List<ItemEntity> videos = recommendVideos(gameIds.subList(0, gameSize), perGameListSize, exclusions);


        return new TypeGroupedItemList(streams, videos, clips);
    }


    private List<ItemEntity> recommendStreams(List<String> gameIds, Set<String> exclusions) {
        List<Stream> streams = twitchService.getStreams(gameIds, PER_PAGE_ITEM_SIZE);
        ////因为twitch API对getStreams是支持传多个gameId的
        List<ItemEntity> resultItems = new ArrayList<>();
        for (Stream stream: streams) {
            if (!exclusions.contains(stream.id())) { //除掉用户点赞的内容给你推荐其他相关的
                resultItems.add(new ItemEntity(stream));
            }
        }
        return resultItems;
    }


    private List<ItemEntity> recommendVideos(List<String> gameIds, int perGameListSize, Set<String> exclusions) {
        List<ItemEntity> resultItems = new ArrayList<>();
        for (String gameId : gameIds) {//因为twitch API对getVideos不支持传多个gameId,所以要再套一层for loop,挨个去搜
            List<Video> listPerGame = twitchService.getVideos(gameId, perGameListSize);
            //perGameListSize就是PER_PAGE_ITEM_SIZE除以gameIds的个数,平均分配一下个数
            for (Video video : listPerGame) {
                if (!exclusions.contains(video.id())) {
                    resultItems.add(new ItemEntity(gameId, video));
                }
            }
        }
        return resultItems;
    }


    private List<ItemEntity> recommendClips(List<String> gameIds, int perGameListSize, Set<String> exclusions) {
        List<ItemEntity> resultItem = new ArrayList<>();
        for (String gameId : gameIds) {//因为twitch API对getClips不支持传多个gameId,所以要再套一层for loop,挨个来搜
            List<Clip> listPerGame = twitchService.getClips(gameId, perGameListSize);
            for (Clip clip : listPerGame) {
                if (!exclusions.contains(clip.id())) {
                    resultItem.add(new ItemEntity(clip));
                }
            }
        }
        return resultItem;
    }
}
