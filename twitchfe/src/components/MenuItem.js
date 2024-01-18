import { Menu } from "antd";
import React from "react";

function MenuItem({ items }) {
  //.map是array里的函数, items因为是复数,所以这个props是个array, map接受的参数也是个函数,这就是call back function
  //?是undefined保护,如果items是null或者undefined那么就不会发生crush,会停在这里不会执行
  //[item1, item2, item3].map(func) 就等于 [funct(item1), funct(item2), funct(item3)], 形成一个新的array
  //就是将items数组里面的每一个item分别转换成一个Menu.Item jsx
  return items?.map((item) => (
    //key是必须填的, 目的是给同一种但细节不同的item进行区分,需要用一个unique的id来进行, 这样re-render的时候不会乱
    //target="_blank"意思是当点击a tag的时候会跳一个新窗口出来
    //rel="noopener noreferrer"是新窗口无法知道你是从哪一个原链接跳转过来的, 为了privacy,对网页进行隔断
    <Menu.Item key={item.id}>
      <a href={item.url} target="_blank" rel="noopener noreferrer">
        {`${item.broadcaster_name} - ${item.title}`}
      </a>
    </Menu.Item>
  ));
}

export default MenuItem;
