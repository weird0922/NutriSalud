package com.nutriSalud.nutri.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String status;
    private T data;
    private String message;
    private Integer code;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status("success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> fail(T data) {
        return ApiResponse.<T>builder()
                .status("fail")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, Integer code) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .code(code)
                .build();
    }

    public static <T> ApiResponse<T> successWithStatus(T data, String info) {
        ApiResponse<T> response = success(data);
        return response;
    }
}
