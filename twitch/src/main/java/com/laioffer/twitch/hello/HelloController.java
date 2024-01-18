package com.laioffer.twitch.hello;

import com.github.javafaker.Faker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.Locale;


@RestController //告诉springboot来看这个class文件去找对应的mapping路径
public class HelloController {

    @GetMapping("/hello") //这个是一个访问路径,一个mapping
    public Person sayHello(@RequestParam(required = false) String locale) {
        if (locale == null) {
            locale = "en_US";
        }
        Faker faker = new Faker(new Locale(locale));
        //用这个faker来帮我们随机的生成一些动态的数据
        String name = faker.name().fullName();
        String company = faker.company().name();
        String street = faker.address().streetAddress();
        String city = faker.address().city();
        String state = faker.address().state();
        String bookTitle = faker.book().title();
        String bookAuthor = faker.book().author();
        String template = "This is %s. I work at %s. I live at %s in %s %s. My favorite book is %s by %s.";
        //%s就是一个占位符, /n是一个换行符

        return new Person(
                name,
                company,
                new Address(street, city, state, null),
                new Book(bookTitle, bookAuthor)
        );

    }
}
