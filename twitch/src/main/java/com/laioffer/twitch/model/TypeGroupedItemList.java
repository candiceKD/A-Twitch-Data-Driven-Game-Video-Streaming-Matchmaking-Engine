package com.laioffer.twitch.model;

import com.laioffer.twitch.db.entity.ItemEntity;
import com.laioffer.twitch.external.model.Clip;
import com.laioffer.twitch.external.model.Stream;
import com.laioffer.twitch.external.model.Video;
import com.laioffer.twitch.model.ItemType;

import java.util.ArrayList;
import java.util.List;

public record TypeGroupedItemList(
        List<ItemEntity> streams, //这三个是反馈给前端的, 为什么一定要用ItemEntity这个类型,是因为我们要从twitch拿到数据,然后存到数据库中
        List<ItemEntity> videos,
        List<ItemEntity> clips
) {

    public TypeGroupedItemList(List<ItemEntity> items) {
        //这个constructor call了下面的function来分拣不同的item, 返回给record里面那三个list
        this(
                filterForType(items, ItemType.STREAM),
                filterForType(items, ItemType.VIDEO),
                filterForType(items, ItemType.CLIP)
        );
    }


    public TypeGroupedItemList(String gameId, List<Stream> streams, List<Video> videos, List<Clip> clips) {
        //这个constructor是用来转换数据类型的,把 Stream , Video, Clip类型转换为ItemEntity
        //ItemEntity这个class的constructor是用来转换类型的, 就是用这个constructor在下面的function里new ItemEntity类型的instance
        //就是用下面三个private function转换的
        this(
                fromStreams(streams),
                fromVideos(gameId, videos),
                fromClips(clips)
        );
    }

    private static List<ItemEntity> filterForType(List<ItemEntity> items, ItemType type) {
        List<ItemEntity> filtered = new ArrayList<>();
        for (ItemEntity item : items) {
            if (item.type() == type) {
                filtered.add(item);
            }
        }
        return filtered;
    }


    private static List<ItemEntity> fromStreams(List<Stream> streams) {
        List<ItemEntity> items = new ArrayList<>();
        for (Stream stream : streams) {
            items.add(new ItemEntity(stream)); //调用的是ItemEntity这个class的constructor来new新的instance,来转换类型
        }
        return items;
    }


    private static List<ItemEntity> fromVideos(String gameId, List<Video> videos) {
        List<ItemEntity> items = new ArrayList<>();
        for (Video video : videos) {
            items.add(new ItemEntity(gameId, video));
        }
        return items;
    }


    private static List<ItemEntity> fromClips(List<Clip> clips) {
        List<ItemEntity> items = new ArrayList<>();
        for (Clip clip : clips) {
            items.add(new ItemEntity(clip));
        }
        return items;
    }

}
