package org.tnf.concurrentframework.example.entity;


import lombok.Builder;

@Builder
public class ResponseResult<T> {
    private Long timestamp;
    private String status;
    private String message;
    private T data;

    public static <T> ResponseResult<T> success(T data) {
        return ResponseResult.<T>builder()
                .data(data)
                .status(ResponseStatus.HTTP_STATUS_200.getResponseCode())
                .message(ResponseStatus.HTTP_STATUS_200.getDescription())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> ResponseResult<T> fail(String status) {
        return ResponseResult.<T>builder()
                .status(ResponseStatus.HTTP_STATUS_500.getResponseCode())
                .message(ResponseStatus.HTTP_STATUS_500.getDescription())
                .timestamp(System.currentTimeMillis())
                .build();
    }


}
