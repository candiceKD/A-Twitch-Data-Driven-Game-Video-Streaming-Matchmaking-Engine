import React from "react";
import { Button, Card, List, message, Tabs, Tooltip } from "antd";
import { StarOutlined, StarFilled } from "@ant-design/icons";
import { addFavoriteItem, deleteFavoriteItem } from "../utils";

const { TabPane } = Tabs;

const tabKeys = {
  Streams: "streams",
  Videos: "videos",
  Clips: "clips",
};
//定义一个常量,来帮我们不会拼写错误,可以直接点操作去找 String enum in TypedScript

const processUrl = (url) =>
  url //这个是来处理链接里面的缩略图的大小的函数
    .replace("%{height}", "252")
    .replace("%{width}", "480")
    .replace("{height}", "252")
    .replace("{width}", "480");

const renderCardTitle = (item, loggedIn, favs = [], favOnChange) => {
  //favs的源头是APP.js里的const [favoriteItems, setFavoriteItems] = useState([]);
  const title = `${item.broadcaster_name} - ${item.title}`;

  const isFav = favs.find((fav) => fav.twitch_id === item.twitch_id);
  //find是一个buildin函数, 浏览器自带的, find括号里面是一个我们定义的callBack function, 教find如何去找的

  const favOnClick = () => {
    if (isFav) {
      deleteFavoriteItem(item) //这一步是从数据库中删除
        .then(() => {
          favOnChange(); //这一步是从前端删除
          //为什么要调用这个function,因为当成功删除或增加的时候需要更新收藏夹,收藏夹是favs
        })
        .catch((err) => {
          message.error(err.message);
        });
      return;
    }

    addFavoriteItem(item)
      .then(() => {
        favOnChange();
      })
      .catch((err) => {
        message.error(err.message);
      });
  };

  return (
    <>
      {loggedIn && (
        <Tooltip
          title={isFav ? "Remove from favorite list" : "Add to favorite list"}
        >
          <Button
            shape="circle"
            icon={isFav ? <StarFilled /> : <StarOutlined />}
            onClick={favOnClick} //让favorite这个button做事情, 做的事情要么就是移除要么就是增加
          />
        </Tooltip>
      )}
      <div style={{ overflow: "hidden", textOverflow: "ellipsis", width: 450 }}>
        <Tooltip title={title}>
          <span>{title}</span>
        </Tooltip>
      </div>
    </>
  );
};

const renderCardGrid = (data, loggedIn, favs, favOnChange) => {
  //这个函数return一堆JSX
  return (
    <List
      grid={{
        //不同尺寸的屏幕放几个
        xs: 1,
        sm: 2,
        md: 4,
        lg: 4,
        xl: 6,
      }}
      //dataSource是props的名字
      dataSource={data}
      renderItem={(item) => (
        //这个有点像.map, 把data里面的每一个元素都转化成一个JSX
        <List.Item style={{ marginRight: "20px" }}>
          <Card title={renderCardTitle(item, loggedIn, favs, favOnChange)}>
            <a //<a> tag就是超级链接标签
              href={item.url}
              target="_blank"
              rel="noopener noreferrer"
              style={{ width: "100%", height: "100%" }}
            >
              <img //<img> tag放在<a> 链接tag里,因为我们想要点击图片能弹出链接, <img>这个tag只有一个不是一对,所以里面不能加其他的东西
                alt="Placeholder" //alt是alternative description
                src={processUrl(item.thumbnail_url)}
                style={{ width: "100%", height: "100%" }}
              />
            </a>
          </Card>
        </List.Item>
      )}
    />
  );
};

const Home = ({ resources, loggedIn, favoriteItems, favoriteOnChange }) => {
  //前三个props都是APP.js传给它的,最后那个是APP.js传给它的function
  const { videos, streams, clips } = resources;
  const {
    videos: favVideos,
    streams: favStreams,
    clips: favClips,
  } = favoriteItems;
  //de-structuring

  return (
    //Tab分页器     //defaultActiveKey这个是分页器的初始值,先显示谁,这个由自己来定
    //tab=""这个是分页上面的标题名字
    //forceRender={true} 意思是几个tab的component都会预先加载,如果是false,那只有点到哪个tab才会加载哪个component
    //renderCardGrid是个函数,不是一个component
    <Tabs defaultActiveKey={tabKeys.Streams}>
      <TabPane tab="Streams" key={tabKeys.Streams} forceRender={true}>
        {renderCardGrid(streams, loggedIn, favStreams, favoriteOnChange)}
      </TabPane>
      <TabPane tab="Videos" key={tabKeys.Videos} forceRender={true}>
        {renderCardGrid(videos, loggedIn, favVideos, favoriteOnChange)}
      </TabPane>
      <TabPane tab="Clips" key={tabKeys.Clips} forceRender={true}>
        {renderCardGrid(clips, loggedIn, favClips, favoriteOnChange)}
      </TabPane>
    </Tabs>
  );
};

export default Home;
