package org.tnf.concurrentframework.seckill.controller;

import org.springframework.web.bind.annotation.*;
import org.tnf.concurrentframework.seckill.dto.UserDTO;
import org.tnf.concurrentframework.seckill.service.UserService;
import org.tnf.concurrentframework.seckill.vo.UserVO;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private UserService userService;

    UserController() {

    }

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public UserVO login(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO);
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("Authorization") String sessionId) {
        userService.logout(sessionId);
        return "Logout successful";
    }

    public UserVO register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }
    
}
