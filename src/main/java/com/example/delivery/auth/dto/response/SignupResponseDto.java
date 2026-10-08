package com.example.delivery.auth.dto.response;

import com.example.delivery.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResponseDto { // Note: record 타입으로 변경 가능하지만 Dto들의 형식을 맞추기 위하여 class로 유지
    private final Long userId;
    private final String loginId;
    private final String username;
    private final UserRole userRole;
}
