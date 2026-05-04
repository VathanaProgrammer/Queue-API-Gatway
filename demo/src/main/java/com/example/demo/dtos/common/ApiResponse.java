package com.example.demo.dtos.common;

import com.fasterxml.jackson.annotation.JsonInclude;

// @JsonInclude បញ្ជាក់ថា បើ Data null វាមិនបាច់បង្ហាញក្នុង JSON ទេ
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean s,
        String msg,
        T data
) {
    // មិនបាច់ដាក់ boolean s ក្នុង parameter ទេ
    public static <T> ApiResponse<T> success(String msg, T data) {
        return new ApiResponse<>(true, msg, data);
    }

    // ដូចគ្នាសម្រាប់ error គឺយើងដឹងថា s ត្រូវតែ false
    public static <T> ApiResponse<T> error(String msg) {
        return new ApiResponse<>(false, msg, null);
    }
}