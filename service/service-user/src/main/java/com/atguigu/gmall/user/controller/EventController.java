package com.atguigu.gmall.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventController {

    @Autowired
    SimpleApplicationEventMulticaster multicaster; //事件派发器

}
