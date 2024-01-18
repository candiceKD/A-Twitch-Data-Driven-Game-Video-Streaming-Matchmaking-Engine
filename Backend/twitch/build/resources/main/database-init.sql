DROP TABLE IF EXISTS favorite_records;
DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS users;


CREATE TABLE users
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,   --     自动创建id
    username VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    password VARCHAR(100) NOT NULL,
    enabled  TINYINT      NOT NULL DEFAULT 1
-- 这个是spring security里面需要的, 代表用户在系统中active, tinyint是指一个字节一个byte
);


CREATE TABLE authorities
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username  VARCHAR(50) NOT NULL,
    authority VARCHAR(50) NOT NULL,  -- 创建一个user就会创建一个对应的authority
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE items
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    twitch_id VARCHAR(255) UNIQUE NOT NULL,
    title TEXT,
--     text比较适合一长段description,不是用来搜索query的
    url VARCHAR(255),
    thumbnail_url VARCHAR(255),
--     图片的url
    broadcaster_name VARCHAR(255),
    game_id VARCHAR(255),
    type VARCHAR(255)
);


CREATE TABLE favorite_records
(
    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    item_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    UNIQUE KEY unique_item_and_user_combo (item_id, user_id)
-- combo unique, 一个用户只能喜欢一个item一次
);
