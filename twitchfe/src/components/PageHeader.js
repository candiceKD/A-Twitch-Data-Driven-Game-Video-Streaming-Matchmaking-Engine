import { Layout, Row, Col, Button } from "antd";
import Favorites from "./Favorites";
import Register from "./Register";
import Login from "./Login";
import React from "react";

const { Header } = Layout;

function PageHeader({
  loggedIn, //这个props是从底层传过来的, 底层是APP.js, PageHeader不负责维护这个props,只负责使用
  signoutOnClick,
  signinOnSuccess,
  favoriteItems,
}) {
  return (
    <Header>
      <Row justify="space-between">
        <Col>{loggedIn && <Favorites favoriteItems={favoriteItems} />}</Col>
        <Col>
          {loggedIn && ( //当用户已经是登录状态的时候才会把右边这一堆表达式返回, 这是个trusy,只要是非0的数字非空的状态都是true,
            <Button shape="round" onClick={signoutOnClick}>
              Logout
            </Button>
          )}
          {!loggedIn && ( //onSucess这个props的等号右边这又是一个props, 这是个falsy
            <>
              <Login onSuccess={signinOnSuccess} />
              <Register />
            </>
          )}
        </Col>
      </Row>
    </Header>
  );
}

export default PageHeader;
