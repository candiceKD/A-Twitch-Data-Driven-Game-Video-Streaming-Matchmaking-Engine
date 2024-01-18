package com.laioffer.twitch.db.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.laioffer.twitch.external.model.Clip;
import com.laioffer.twitch.external.model.Stream;
import com.laioffer.twitch.external.model.Video;
import com.laioffer.twitch.model.ItemType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Table("items")
//这个是告诉spring 如何用这个class, 对应的是items那个table, 就是一个database的mapping
public record ItemEntity(  //在record里面这些member filed就是constructor
        @Id Long id,
//        加这个@Id是为了告诉 JDBC 这个是primary key
        @JsonProperty("twitch_id") String twitchId,
        String title,
        String url,
        @JsonProperty("thumbnail_url") String thumbnailUrl,
        @JsonProperty("broadcaster_name") String broadcasterName,
        @JsonProperty("game_id") String gameId,
        @JsonProperty("item_type") ItemType type
) {


    public ItemEntity(String gameId, Video video) {
        this(null, video.id(), video.title(), video.url(), video.thumbnailUrl(), video.userName(), gameId, ItemType.VIDEO);
    } //这是call record那个constructor去做一个overload,
     // id为null,是指我们从twitch把这些item拿过来的时候不知道这些item存在database中的id,所以为null, 后面database里面会自动生成id


    public ItemEntity(Clip clip) {
        this(null, clip.id(), clip.title(), clip.url(), clip.thumbnailUrl(), clip.broadcasterName(), clip.gameId(), ItemType.CLIP);
    }


    public ItemEntity(Stream stream) {
        this(null, stream.id(), stream.title(), null, stream.thumbnailUrl(), stream.userName(), stream.gameId(), ItemType.STREAM);
    }
}
