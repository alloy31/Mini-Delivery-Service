package com.example.delivery.global.security;

import com.example.delivery.user.entity.UserRole;

import java.security.Principal;

//jwt에서 추출한 사용자의 정보를 담기위한 클래스

public record JwtPrincipal( //
        Long userId,
        String loginId,
        UserRole role
) implements Principal {

    @Override
    public String getName() {
        return loginId;
    }
}

// Note: record는 몇가지 값을 묶어서 전달하는 객체를 간결하게 만드는 java class
// 검증한 사용자 정보를 전달'만' 하기 위한 객체