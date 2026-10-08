package com.example.delivery.auth.controller;

import com.example.delivery.auth.dto.request.QuitRequestDto;
import com.example.delivery.auth.dto.request.SigninRequestDto;
import com.example.delivery.auth.dto.request.SignupRequestDto;
import com.example.delivery.auth.dto.response.QuitResponseDto;
import com.example.delivery.auth.dto.response.SigninResponseDto;
import com.example.delivery.auth.dto.response.SignupResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    //회원가입
    @PostMapping("/signup")
    public SignupResponseDto signup(@Valid @RequestBody SignupRequestDto dto){
    // Note: Valid를 Dto 클래스 내에 붙이면 미리 검증하고 오지 않을까? -> No No 클래스 내부에는 검증 규칙을 두고, 컨트롤러에서 Valid로 검사 수행
    // Dto가 Dto를 포함할때는 Dto 클래스 내부의 Dto에 valid를 붙이면 검사가 전파된다.

        return null;
    }

    //로그인
    @PostMapping("/signin")
    public SigninResponseDto signin(@Valid @RequestBody SigninRequestDto dto){

        return null;
    }

    //회원탈퇴
    @PostMapping("/quit")
    public QuitResponseDto quit(@Valid @RequestBody QuitRequestDto dto){
        return null;
    }

    //비밀번호 재설정

}
