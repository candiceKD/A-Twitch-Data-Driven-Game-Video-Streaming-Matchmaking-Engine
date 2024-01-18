import React, { useState, useEffect } from "react";
import { Layout, message, Menu } from "antd";
import { LikeOutlined, FireOutlined } from "@ant-design/icons";
import {
  logout,
  getFavoriteItem,
  getTopGames,
  searchGameById,
  getRecommendations,
} from "./utils";
import PageHeader from "./components/PageHeader";
import CustomSearch from "./components/CustomSearch";
import Home from "./components/Home";

const { Header, Content, Sider } = Layout;

function App() {
  const [loggedIn, setLoggedIn] = useState(false);
  const [favoriteItems, setFavoriteItems] = useState([]);
  const [topGames, setTopGames] = useState([]);
  const [resources, setResources] = useState({
    //Home component的难点就是如何把resources这个state上的所有数据都显示出来
    videos: [],
    streams: [],
    clips: [],
  });

  useEffect(() => {
    getTopGames()
      .then((data) => {
        setTopGames(data);
      })
      .catch((err) => {
        message.error(err.message);
      });
  }, []); //useEffect这个函数只在Mount的时候运行一次, 因为后面传入的是个空的dependency array, 成功之后把数据放在state上

  const signinOnSuccess = () => {
    setLoggedIn(true);
    getFavoriteItem().then((data) => {
      setFavoriteItems(data);
    });
  };

  const signoutOnClick = () => {
    logout() //这个logout函数是utils里面前后端交流的那个logout function
      .then(() => {
        setLoggedIn(false);
        message.success("Successfully Signed out");
      })
      .catch((err) => {
        message.error(err.message);
      });
  };

  const customSearchOnSuccess = (data) => {
    setResources(data); //用拿到的data去更新Resources这个state
  };

  const onGameSelect = ({ key }) => {
    if (key === "recommendation") {
      getRecommendations().then((data) => {
        setResources(data);
      });
      return;
    }
    searchGameById(key).then((data) => {
      setResources(data);
    });
  };

  //这个函数如何更新前端的:当修改完收藏夹,让前端去调一次API,重新从DB中把收藏夹的数据拉一遍新的出来,而不是让前端去判断哪个元素被修改了
  const favoriteOnChange = () => {
    //添加和删除是在App.js这一层发生的, 所以favoriteOnChange也写在这一层,但是这个function是在Home.js里被调用的,所以要把这个函数和props传参传给Home.js
    getFavoriteItem()
      .then((data) => {
        setFavoriteItems(data);
      })
      .catch((err) => {
        message.error(err.message);
      });
  };

  const mapTopGamesToProps = (topGames) => [
    {
      //这个组装函数分为两部分,上面是确定的, 是recommendation, 下面是动态的
      label: "Recommend for you!",
      key: "recommendation",
      icon: <LikeOutlined />,
    },
    {
      label: "Popular Games",
      key: "popular_games",
      icon: <FireOutlined />,
      children: topGames.map((game) => ({
        //把topGames里面的每一个game元素拿出来形成一个新的object
        label: game.name,
        key: game.id, //unique identify
        icon: (
          <img
            alt="placeholder"
            src={game.box_art_url //缩略图
              .replace("{height}", "40")
              .replace("{width}", "40")}
            style={{ borderRadius: "50%", marginRight: "20px" }}
            //当宽高一样,borderRadius取50%刚好是个圆形
          />
        ),
      })),
    },
  ];

  return (
    <Layout>
      <Header>
        <PageHeader
          loggedIn={loggedIn}
          signoutOnClick={signoutOnClick} //这个signout props也往下传了一层,传给了PageHeader
          signinOnSuccess={signinOnSuccess} //这个props对应一个function,这个function把loggedIn这个state变成true
          favoriteItems={favoriteItems}
        />
      </Header>
      <Layout>
        <Sider width={300} className="site-layout-background">
          <CustomSearch onSuccess={customSearchOnSuccess} />
          <Menu //搜索成功之后的数据要存在一个state里, 这个state就是resource
            mode="inline" // inline就是缩在一起的排列而不是飞出来的
            onSelect={onGameSelect}
            style={{ marginTop: "10px" }}
            items={mapTopGamesToProps(topGames)}
          />
        </Sider>
        <Layout style={{ padding: "24px" }}>
          <Content
            className="site-layout-background"
            style={{
              //html box-model
              padding: 24,
              margin: 0,
              height: 800,
              overflow: "auto", //溢出,一旦超过800的高度就会使用滚动条
            }}
          >
            <Home //props不止能传数据也可以传方法
              resources={resources} //这里传的是数据
              loggedIn={loggedIn} //这里传的也是数据,因为favorite component所以Home这里显示的数据跟logIn的状态有关
              favoriteOnChange={favoriteOnChange} //
              favoriteItems={favoriteItems} //这里传的也是数据
            />
          </Content>
        </Layout>
      </Layout>
    </Layout>
  );
}

export default App;
