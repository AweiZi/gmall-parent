package com.atguigu.gmall.search;

import com.atguigu.gmall.search.bean.Person;
import com.atguigu.gmall.search.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EsTest {
    @Autowired
    PersonRepository personRepository;


    @Test
    void saveTest() {
        Person person = new Person();
        person.setId(1L);
        person.setFirstName("高");
        person.setLastName("鹏伟");
        person.setAge(14);
        person.setAddress("陕西省郭博洋心中");

        Person person1 = new Person();
        person1.setId(2L);
        person1.setFirstName("博洋");
        person1.setLastName("郭");
        person1.setAge(14);
        person1.setAddress("陕西省高鹏伟心中");

        Person person2 = new Person();
        person2.setId(3L);
        person2.setFirstName("三");
        person2.setLastName("张");
        person2.setAge(154);
        person2.setAddress("陕西省高鹏伟心中");

        personRepository.save(person);
        personRepository.save(person1);
        personRepository.save(person2);

        System.out.println("完成所有save");
    }
}
