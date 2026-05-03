package com.example.demo.DTO;

public record OrderRequest (String userId, String productName) {
    public String getUserId() {return userId;}
    public String getProductName() {return productName;}
}
