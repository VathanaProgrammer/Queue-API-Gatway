package com.example.demo.services;

import com.example.demo.DTO.UserRequest;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public ResponseEntity<?> createUser(UserRequest user) {
        String name = user.getName().trim();
        if(name.isEmpty()){
            return ResponseEntity.badRequest().body("Name is required");
        }

        if(name.length() < 3){
            return ResponseEntity.badRequest().body("Name must be at least 3 characters long");
        }

        if(userRepository.existsByName(name)){
            return ResponseEntity.badRequest().body("User already exists");
        }

        User newUser = new User(name);
        userRepository.save(newUser);

        Map<String, Object> response = new HashMap<>();
        response.put("user", newUser);
        response.put("message", "User created successfully");

        return ResponseEntity.status(201).body(response);
    }
}
