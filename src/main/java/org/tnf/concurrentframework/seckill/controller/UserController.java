package org.tnf.concurrentframework.seckill.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tnf.concurrentframework.seckill.dao.UserMapper;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private UserMapper userMapper;

    UserController() {

    }

    UserController(UserMapper usermapper) {
        this.userMapper = usermapper;
    }


}
