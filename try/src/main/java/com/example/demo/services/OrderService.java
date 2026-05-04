package com.example.demo.services;

import com.example.demo.DTO.OrderRequest;
import com.example.demo.models.Order;
import com.example.demo.models.User;
import com.example.demo.repositories.OrderRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderService {

    private OrderRepository orderRepository;

    private UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> createOrder(OrderRequest orderRequest) {
        String productName = orderRequest.getProductName();
        String userId = orderRequest.getUserId();
        User user = userRepository.findById(userId).orElseThrow();
        System.out.println("The value of product name: " + productName);
        System.out.println("The value of user id: " + userId);

        orderRepository.save(new Order(user, productName));

        return ResponseEntity.ok().body("Order created successfully");
    }

}
