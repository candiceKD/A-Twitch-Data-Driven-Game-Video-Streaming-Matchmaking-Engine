package com.laioffer.twitch;

import com.laioffer.twitch.db.FavoriteRecordRepository;
import com.laioffer.twitch.db.ItemRepository;
import com.laioffer.twitch.db.entity.FavoriteRecordEntity;
import com.laioffer.twitch.db.entity.ItemEntity;
import com.laioffer.twitch.db.entity.UserEntity;
import com.laioffer.twitch.favorite.FavoriteService;
import com.laioffer.twitch.model.DuplicateFavoriteException;
import com.laioffer.twitch.model.ItemType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
//加一些behavior, 一个unit test里面的library叫Mockito
public class FavoriteServiceTests {

//@Mock 把要测试的部分的dependency丢进来, 用Mock换成假的,只是一个傀儡
    @Mock private ItemRepository itemRepository;
    @Mock private FavoriteRecordRepository favoriteRecordRepository;


    @Captor ArgumentCaptor<FavoriteRecordEntity> favoriteRecordArgumentCaptor;
    //这行代码的意思是：
    //定义了一个名为 favoriteRecordArgumentCaptor 的变量。
    //这个变量的类型是 ArgumentCaptor<FavoriteRecordEntity>，意味着它是用来捕获 FavoriteRecordEntity 类型对象的参数。
    //@Captor 是一个注解，用于标记这个变量应该被 Mockito 框架所初始化。


    private FavoriteService favoriteService;


    @BeforeEach
    //@BeforeEach 注解用于标记一个方法，以确保这个方法在每一个测试方法之前运行。
    //自己new了一个传入dependency, 并不是真的去连了DB,只是假的测试一下
    public void setup() {
        favoriteService = new FavoriteService(itemRepository, favoriteRecordRepository);
    }


    @Test
    public void whenItemNotExist_setFavoriteItem_shouldSaveItem() throws DuplicateFavoriteException {
        //这里ItemNotExist所以test会把itemRepository拿到的item设为null, 去测试itemRepository.save这个功能,而不管db这个item存没存成功
        UserEntity user = new UserEntity(1L, "user", "foo", "bar", "123456");
        ItemEntity item = new ItemEntity(null, "twitchId", "title", "url", "thumb", "broadcaster", "gameid", ItemType.VIDEO);
        ItemEntity persisted = new ItemEntity(1L, "twitchId", "title", "url", "thumb", "broadcaster", "gameid", ItemType.VIDEO);
        Mockito.when(itemRepository.findByTwitchId("twitchId")).thenReturn(null);
        Mockito.when(itemRepository.save(item)).thenReturn(persisted);
        //为啥thenReturn(persisted) 得让这个代码能运行下去


        favoriteService.setFavoriteItem(user, item);


        Mockito.verify(itemRepository).save(item);
        //去verify 这个itemRepository.save被call过了
    }


    @Test
    public void whenItemExist_setFavoriteItem_shouldNotSaveItem() throws DuplicateFavoriteException {
        UserEntity user = new UserEntity(1L, "user", "foo", "bar", "123456");
        ItemEntity item = new ItemEntity(null, "twitchId", "title", "url", "thumb", "broadcaster", "gameid", ItemType.VIDEO);
        ItemEntity persisted = new ItemEntity(1L, "twitchId", "title", "url", "thumb", "broadcaster", "gameid", ItemType.VIDEO);
        Mockito.when(itemRepository.findByTwitchId("twitchId")).thenReturn(persisted);


        favoriteService.setFavoriteItem(user, item);


        Mockito.verify(itemRepository, Mockito.never()).save(item);
        // Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any(ItemEntity.class));
        //改写成这样能cover所有的不管什么ItemEntity都不会被save
        //去verify 这个itemRepository.save不会被call
    }


    @Test
    public void setFavoriteItem_shouldCreateFavoriteRecord() throws DuplicateFavoriteException {
        UserEntity user = new UserEntity(1L, "user", "foo", "bar", "123456");
        ItemEntity item = new ItemEntity(null, "twitchId", "title", "url", "thumb", "broadcaster", "gameid", ItemType.VIDEO);
        ItemEntity persisted = new ItemEntity(10L, "twitchId", "title", "url", "thumb", "broadcaster", "gameid", ItemType.VIDEO);
        Mockito.when(itemRepository.findByTwitchId("twitchId")).thenReturn(persisted);


        favoriteService.setFavoriteItem(user, item);


        Mockito.verify(favoriteRecordRepository).save(favoriteRecordArgumentCaptor.capture());
        //verify favoriteRecordRepository 里面save了这条favoriteRecord
        //用这个favoriteRecordArgumentCaptor.capture()的原因应该是这是运行过程中产生的,
        // //不是像item和persistItem我们已经给定好的, 所以要capture一下
        FavoriteRecordEntity favorite = favoriteRecordArgumentCaptor.getValue();


        Assertions.assertEquals(1L, favorite.itemId());
        Assertions.assertEquals(1L, favorite.userId());
        //拿到这个record去assert一下itemId和userId是不是跟我们expected的一样
    }


    @Test
    public void whenItemNotExist_unsetFavoriteItem_shouldNotDeleteFavoriteRecord() {
        UserEntity user = new UserEntity(1L, "user", "foo", "bar", "123456");
        Mockito.when(itemRepository.findByTwitchId("twitchId")).thenReturn(null);


        favoriteService.unsetFavoriteItem(user, "twitchId");


        Mockito.verifyNoInteractions(favoriteRecordRepository);
    }


    @Test
    public void whenItemExist_unsetFavoriteItem_shouldDeleteFavoriteRecord() {
        UserEntity user = new UserEntity(1L, "user", "foo", "bar", "123456");
        ItemEntity persisted = new ItemEntity(10L, "twitchId", "title", "url", "thumb", "broadcaster", "gameid", ItemType.VIDEO);
        Mockito.when(itemRepository.findByTwitchId("twitchId")).thenReturn(persisted);


        favoriteService.unsetFavoriteItem(user, "twitchId");


        Mockito.verify(favoriteRecordRepository).delete(1L, 10L);
    }
}

