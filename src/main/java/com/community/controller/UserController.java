package com.community.controller;

import com.community.entity.User;
import com.community.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 모든 사용자 조회
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // 사용자 추가
    @PostMapping
    public String createUser(@RequestBody User user) {
        userService.createUser(user);
        return "사용자가 성공적으로 추가되었습니다!";
    }
}

