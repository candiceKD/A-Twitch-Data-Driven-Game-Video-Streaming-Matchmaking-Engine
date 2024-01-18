package com.laioffer.twitch;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableCaching
@EnableFeignClients  //告诉springboot要enable openFeign
@SpringBootApplication
public class TwitchApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitchApplication.class, args);
	}
}



