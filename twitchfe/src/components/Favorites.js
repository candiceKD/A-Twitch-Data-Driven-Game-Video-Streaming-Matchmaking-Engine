import React, { useState } from "react";
import MenuItem from "./MenuItem";
import { Menu, Button, Drawer } from "antd";
import {
  EyeOutlined,
  YoutubeOutlined,
  VideoCameraOutlined,
  StarFilled,
} from "@ant-design/icons";

const { SubMenu } = Menu;

function Favorites({ favoriteItems }) {
  //一个props
  const [displayDrawer, setDisplayDrawer] = useState(false);
  const { videos, streams, clips } = favoriteItems; //这是个de-structuring, 把favoriteItems这个props拆解开

  const onFavoriteClick = () => {
    setDisplayDrawer(true);
  };

  const onDrawerClose = () => {
    setDisplayDrawer(false);
  };

  return (
    <>
      <Button
        type="primary"
        shape="round"
        onClick={onFavoriteClick}
        icon={<StarFilled />}
      >
        My Favorites
      </Button>
      <Drawer //弹出来的界面
        title="My Favorites"
        placement="right" //从哪个方向弹出来
        width={720}
        visible={displayDrawer}
        onClose={onDrawerClose}
      >
        <Menu
          mode="inline"
          defaultOpenKeys={["streams"]}
          style={{ height: "100%", borderRight: 0 }}
          selectable={false}
        >
          <SubMenu key={"streams"} icon={<EyeOutlined />} title="Streams">
            <MenuItem items={streams} />
          </SubMenu>
          <SubMenu key={"videos"} icon={<YoutubeOutlined />} title="Videos">
            <MenuItem items={videos} />
          </SubMenu>
          <SubMenu key={"clips"} icon={<VideoCameraOutlined />} title="Clips">
            <MenuItem items={clips} />
          </SubMenu>
        </Menu>
      </Drawer>
    </>
  );
}

export default Favorites;
