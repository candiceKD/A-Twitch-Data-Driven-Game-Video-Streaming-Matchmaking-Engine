import { Button, Form, Input, message, Modal } from "antd";
import React, { useState } from "react";
import { LockOutlined, UserOutlined } from "@ant-design/icons";
import { login } from "../utils";

function Login({ onSuccess }) {
  //这个props的写法也是de-structuring const{onSuccess} = props
  //这是个function component, 传入的是props, 这个props是PageHeader传给它的, 所以PageHeader是Login的parent
  const [displayModal, setDisplayModal] = useState(false);

  const handleCancel = () => {
    setDisplayModal(false);
  };

  const signinOnClick = () => {
    setDisplayModal(true);
  };

  const onFinish = (data) => {
    login(data) //这个地方去调用了utils里面的login function, 然后credential就是在这里传入的, 在Form里面收集的数据
      .then(() => {
        setDisplayModal(false);
        message.success(`Welcome back`);
        onSuccess();
      })
      .catch((err) => {
        message.error(err.message);
      });
  };
  //<> 叫React fragment, 语法意义是placeHolder, 当 return里面的JSX处于并列结构没有root的时候,用它来表示root
  //<Modal>会话窗
  return (
    <>
      <Button
        shape="round"
        onClick={signinOnClick}
        style={{ marginRight: "20px" }}
      >
        Login
      </Button>
      <Modal
        title="Log in"
        visible={displayModal} //这是一个state, 通过改变这个state来让Login这个component rerender来控制Modal是开还是关
        onCancel={handleCancel} //通过这个函数使得displayModal变成false
        footer={null} //不要footer那一栏
        destroyOnClose={true} //每次关掉这个Modal <Form>这些component会destroy掉
      >
        <Form name="normal_login" onFinish={onFinish} preserve={false}>
          <Form.Item //第一个小孩:收集username            //preserve是表示当Form destory的时候是否preserve, 只有Form有
            name="username"
            rules={[{ required: true, message: "Please input your Username!" }]}
          >
            <Input prefix={<UserOutlined />} placeholder="Username" />
          </Form.Item>
          <Form.Item //第二个小孩:收集password
            rules={[{ required: true, message: "Please input your Password!" }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="Password" />
          </Form.Item>
          <Form.Item // 第三个小孩触发onFinish这个function, 因为这个button包在Form里面, 所以不用写onClick,而是用htmlType来触发上面的function
          >
            <Button type="primary" htmlType="submit">
              Login
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}

export default Login;
