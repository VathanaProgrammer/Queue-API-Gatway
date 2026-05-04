package com.example.demo.DTO;

public record UserRequest(String name) {
    public String getName() {return name;}
}
