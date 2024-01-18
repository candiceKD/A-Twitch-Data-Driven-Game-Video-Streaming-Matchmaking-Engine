const SERVER_ORIGIN = "";
//本地开发的时候就留空, 如果前后端分开部署的话这里可以填东西

const loginUrl = `${SERVER_ORIGIN}/login`;
//这些常量就是每一个API对应的url

export const login = (credential) => {
  //credential(凭据), 来源:用户输入
  const formData = new FormData(); //global的东西,浏览器自带的function, 这个函数内部会自动补齐headers
  formData.append("username", credential.username); //把credential的两个参数装到formData里面,然后返回给后端
  formData.append("password", credential.password);
  //不是所有的前后端交流都用formData,只有后端用的是formData才用,如果后端用的是QueryString或者Json就变成其他的
  //上面这一步是数据组装

  return fetch(loginUrl, {
    //fetch是个浏览器自带的前后端通信的method
    //这一步是发送请求, loginUrl是请求的目的地, 后面这个object是请求的configuration
    method: "POST",
    credentials: "include", //这个意思是login这个function是允许带cookie的,来回都要带,
    //但因为这里是login, 还没有登录时不能发送cookie的,只能回cookie,所以这是再向后端索要cookie, 加这个就是开启携带cookie的功能
    body: formData,
  }).then((response) => {
    //因为fetch这个函数时unpredictable,不是能够马上给response的, .then给fetch注册一个函数,如果成功就做后面的操作
    if (response.status !== 204) {
      //这个数字要跟后端协作好
      throw Error("Fail to log in");
    }
  }); //里面是个call back function, 这个函数是当.then监听到后端的返回值时候,再调用这个call back function
};

const registerUrl = `${SERVER_ORIGIN}/register`;

export const register = (data) => {
  return fetch(registerUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json", //作用是告诉后端即将发过来的请求是Json格式的
    },
    body: JSON.stringify(data), //因为这个data是string, stringify只是给这些数据拍扁, 但是后端不能自动识别出来是Json格式的
  }).then((response) => {
    if (response.status !== 200) {
      throw Error("Fail to register");
    }
  });
};

const logoutUrl = `${SERVER_ORIGIN}/logout`;

export const logout = () => {
  return fetch(logoutUrl, {
    method: "POST",
    credentials: "include",
  }).then((response) => {
    if (response.status !== 204) {
      throw Error("Fail to log out");
    }
  });
};

const topGamesUrl = `${SERVER_ORIGIN}/game`;

export const getTopGames = () => {
  return fetch(topGamesUrl).then((response) => {
    //fetch不写第二个参数默认http method是get
    if (response.status !== 200) {
      throw Error("Fail to get top games");
    }

    return response.json();
  });
};

const getGameDetailsUrl = `${SERVER_ORIGIN}/game?game_name=`;
// game_name是queryString, 也是一个传参的方式

const getGameDetails = (gameName) => {
  return fetch(`${getGameDetailsUrl}${gameName}`).then((response) => {
    //这个参数是把gameName当作queryString放在url里,
    //这个数据是靠query传的,不是靠request body传的
    if (response.status !== 200) {
      throw Error("Fail to find the game");
    }

    return response.json();
  });
};

const searchGameByIdUrl = `${SERVER_ORIGIN}/search?game_id=`;

export const searchGameById = (gameId) => {
  return fetch(`${searchGameByIdUrl}${gameId}`).then((response) => {
    if (response.status !== 200) {
      throw Error("Fail to find the game");
    }
    return response.json();
  });
};

export const searchGameByName = (gameName) => {
  return getGameDetails(gameName).then((data) => {
    if (data && data[0].id) {
      return searchGameById(data[0].id);
    }

    throw Error("Fail to find the game");
  });
};

const favoriteItemUrl = `${SERVER_ORIGIN}/favorite`;

export const addFavoriteItem = (favItem) => {
  return fetch(favoriteItemUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include", //必须登录才能用,所以要验证cookie
    body: JSON.stringify({ favorite: favItem }),
  }).then((response) => {
    if (response.status !== 200) {
      throw Error("Fail to add favorite item");
    }
  });
};

export const deleteFavoriteItem = (favItem) => {
  return fetch(favoriteItemUrl, {
    method: "DELETE",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify({ favorite: favItem }),
  }).then((response) => {
    if (response.status !== 200) {
      throw Error("Fail to delete favorite item");
    }
  });
};

export const getFavoriteItem = () => {
  return fetch(favoriteItemUrl, {
    credentials: "include",
  }).then((response) => {
    if (response.status !== 200) {
      throw Error("Fail to get favorite item");
    }

    return response.json();
  });
};

const getRecommendedItemsUrl = `${SERVER_ORIGIN}/recommendation`;

export const getRecommendations = () => {
  return fetch(getRecommendedItemsUrl, {
    credentials: "include", //这个是不管登不登陆都可以调用这个API,只不过登陆后可以定制化推荐, 所以登陆后就带cookie
  }).then((response) => {
    if (response.status !== 200) {
      throw Error("Fail to get recommended item");
    }

    return response.json();
  });
};
//每一个后端的API在前端的这个文件里也都对应的写了一个function
//前端代码会指挥用户的浏览器去跟后端的server沟通, 每一个后端沟通就得有一个function
