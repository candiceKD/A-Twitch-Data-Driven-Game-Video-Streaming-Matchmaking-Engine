package com.laioffer.twitch;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;


import javax.sql.DataSource;

@Configuration
public class AppConfig {

    @Bean //这也是spring实现的代码
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //像漏斗一样来过滤检查, 我们在这里只做了path的security检查, 前半部分都是specify 一些path的检查
        //用了fluent API
        http
                .csrf().disable()
                //csrf是跨站点请求伪造Cross Site Request Forgery, 当前后端不在同一个origin的时候不在同一个server上,你发的request就会被阻挡
                //这里我们要disable掉因为测试阶段, 在local本机上运行,前后端不是在一个端口,而deploy之后前后端会放在用一个server上
                .authorizeHttpRequests(auth ->
                        //箭头是lambda 把auth这个参数丢进后面这段代码执行完了,要返回给authorizeHttpRequest这个function去consume,其实就是一个call back
                        //auth是authorizeHttpRequest这个function提供的
                        //这些requestMatcher是层级关系,是priority关系的
                        auth
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                //还没注册就要把前端的所有显示给用户,specify了前端最常见最基础的文件类型都被允许
                                .requestMatchers(HttpMethod.GET, "/", "/index.html", "/*.json", "/*.png", "/static/**").permitAll()
                                //第二行还是做同一件事,把第一行没有specify的前端文件类型在这里specify
                                .requestMatchers("/hello/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/login", "/register", "/logout").permitAll()
                                //得先让这几个API是public才能做后面的操作的Authentication
                                .requestMatchers(HttpMethod.GET, "/recommendation", "/game","/search").permitAll()
                                .anyRequest().authenticated()
                )
                //下面这些是改变login和logout的behavior的, 因为我们用了一些大礼包,我们不用default的那些属性
                //这些都是single page application 前端去做的事
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                //by default,login失败会给我们带回原来的登录页面,但我们不需要,所以改成Unauthorized
                .and()
                .formLogin()
                //我们用的twitch的verify authorization的方法叫Oauth2, 我们要纠正回来, 用formLogin, 就是提交表单的方法, 就是session-based
                .successHandler((req, res, auth) -> res.setStatus(HttpStatus.NO_CONTENT.value()))
                .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                .and()
                .logout()
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT));
        //上面那些都是specify,成功或失败给我返回一个status code或者扔一个error, 而不是帮我redirect
        return http.build();

    }

    @Bean
// 用这个annotation来提供当你没有创建source code但是又要dependency injection,所以就要用Bean
// 当你需要这个dependency,但是你创建的object不是你的source code,就用@Bean
// UserDetailsManager这是一个Interface, 你可以接入各种db的DetailsManager,这里我们用的是JDBC
    //DataSource dataSource也是一个dependency injection
    UserDetailsManager users(DataSource dataSource) {//dataSource也是一个dependency
        return new JdbcUserDetailsManager(dataSource);
    }
    //这个是spring 去check 密码对不对,用户存不存在, 用户名对不对,用户是否被ban的方法

    @Bean
        // 这是一个Interface
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
//这个是来帮你做one-way 密码加密
