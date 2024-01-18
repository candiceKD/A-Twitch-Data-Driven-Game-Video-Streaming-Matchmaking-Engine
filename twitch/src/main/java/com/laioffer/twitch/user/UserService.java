package com.laioffer.twitch.user;

import com.laioffer.twitch.db.UserRepository;
import com.laioffer.twitch.db.entity.UserEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {


    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    public UserService(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }


    @Transactional
    //要保证这个操作要成功就一起成功,如果出错就roll back
    public void register(String username, String password, String firstName, String lastName) {
        UserDetails user = User.builder()
                //这个UserDetails是spring security里面的object, 通过builder方法来build一个user
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles("USER")
                //spring security会自动帮我们写入一个叫authorities的DB table里面,这个table我们不会直接去使用
                .build();
        userDetailsManager.createUser(user);
        userRepository.updateNameByUsername(username, firstName, lastName);
        //协作, 因为UserDetail 不handle除了用户名和密码之外的与authentication无关的信息, 我们可以后面自己在userRepository里面update
    }


    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

