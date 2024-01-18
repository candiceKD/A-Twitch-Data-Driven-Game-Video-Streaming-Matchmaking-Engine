package com.laioffer.twitch.favorite;

import com.laioffer.twitch.db.FavoriteRecordRepository;
import com.laioffer.twitch.db.ItemRepository;
import com.laioffer.twitch.db.entity.FavoriteRecordEntity;
import com.laioffer.twitch.db.entity.ItemEntity;
import com.laioffer.twitch.db.entity.UserEntity;
import com.laioffer.twitch.model.DuplicateFavoriteException;
import com.laioffer.twitch.model.TypeGroupedItemList;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class FavoriteService {
    //FavoriteService主要做的是什么? 设置FavoriteItem, 删除FavoriteItem, 查询FavoriteItem

    //两个dependency, db里面叫repository, 直接接第三方RestAPI的叫client
    //怎么提供进来, 这个技术叫dependency injection
    private final ItemRepository itemRepository;
    private final FavoriteRecordRepository favoriteRecordRepository;

    public FavoriteService(ItemRepository itemRepository,
                           FavoriteRecordRepository favoriteRecordRepository) {
        this.itemRepository = itemRepository;
        this.favoriteRecordRepository = favoriteRecordRepository;
    }

    //当你操作过favorite这个API之后, 会影响recommendationService,那我们要清一下缓存,要不然每次都要等一分钟后才能显示新的结果
    //操纵的是同一个cache空间,注意名字要和recommendation的对的上
    @CacheEvict(cacheNames = "recommend_items", key = "#user")//支持多个用户存不同的cache
    @Transactional
// 这个annotation的意思是 database里面的概念ACID :如果中间出错了,前面存进去的就会吐出来,这就是原子性Atomically,如果失败就进行rollback回滚
    public void setFavoriteItem(UserEntity user, ItemEntity item) throws DuplicateFavoriteException {
        ItemEntity persistedItem = itemRepository.findByTwitchId(item.twitchId());
        //我们去twitch拉回一大堆东西,不是所有的item都保存在DB,只有用户点赞setFavorite才会保存在DB中
        if (persistedItem == null) {
            persistedItem = itemRepository.save(item);
        }
        if (favoriteRecordRepository.existsByUserIdAndItemId(user.id(), persistedItem.id())) {
            throw new DuplicateFavoriteException();
        }
        //如果上面的throw exception发生了,下面的就不会发生了,一种special的return
        FavoriteRecordEntity favoriteRecord = new FavoriteRecordEntity(null, user.id(), persistedItem.id(), Instant.now());
        //Instant.now()就是一个当前的timestamp
        //保存这条记录是先new一个Entity,然后把这个Entity保存到repository里面
        favoriteRecordRepository.save(favoriteRecord);
    }

    //下面的只有一次写操作,所以不需要@Transactional
    @CacheEvict(cacheNames = "recommend_items", key = "#user")
    public void unsetFavoriteItem(UserEntity user, String twitchId) {
        ItemEntity item = itemRepository.findByTwitchId(twitchId);
        if (item != null) {
            favoriteRecordRepository.delete(user.id(), item.id());
        }
    }


    public List<ItemEntity> getFavoriteItems(UserEntity user) {
        List<Long> favoriteItemIds = favoriteRecordRepository.findFavoriteItemIdsByUserId(user.id());
        return itemRepository.findAllById(favoriteItemIds);
    }


    public TypeGroupedItemList getGroupedFavoriteItems(UserEntity user) {
        List<ItemEntity> items = getFavoriteItems(user);
//        这个地方call了上面的function
        return new TypeGroupedItemList(items);
    }
}

