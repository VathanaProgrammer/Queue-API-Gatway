package com.example.demo.dtos;

public record TokenRefreshResponse(String accessToken, String refreshToken) {
    // ក្នុង Record បងមិនបាច់សរសេរ get អីទៀតទេ ព្រោះ Controller បងគ្រាន់តែប្រើ Constructor បង្កើតវា
}
