package org.tnf.concurrentframework.example.controller;

import org.springframework.web.bind.annotation.*;
import org.tnf.concurrentframework.seckill.annotation.SeckillWrapper;
import org.tnf.concurrentframework.example.dto.UserDTO;
import org.tnf.concurrentframework.example.service.UserService;
import org.tnf.concurrentframework.example.vo.UserVO;

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

    @PostMapping("/register")
    public UserVO register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    @SeckillWrapper(topic = "Seckill")
    @GetMapping("/me")
    public UserVO info(@RequestBody UserDTO userDTO) {
        return userService.getUserById(userDTO.getId());
    }
}
