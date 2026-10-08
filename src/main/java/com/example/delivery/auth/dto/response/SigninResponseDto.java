package com.example.delivery.auth.dto.response;

import lombok.Getter;

@Getter
public class SigninResponseDto {
    private final String accessToken;
    private final String tokenType = "Bearer";
    public SigninResponseDto(String accessToken){
        this.accessToken = accessToken;
    }
}
