package com.example.demo.controllers;

import com.example.demo.DTO.UserRequest;
import com.example.demo.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private UserService userService
            ;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/create")
    public ResponseEntity<?> createUser(@RequestBody UserRequest user) {
        System.out.println("The value of request body: " + user.getName());
        return userService.createUser(user);
    }

    @GetMapping("/")
    public String hello() {
        return "Hello World!";
    }
}
