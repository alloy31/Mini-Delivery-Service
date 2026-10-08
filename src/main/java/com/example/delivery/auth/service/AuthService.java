package com.example.delivery.auth.service;

import com.example.delivery.auth.dto.request.SigninRequestDto;
import com.example.delivery.auth.dto.request.SignupRequestDto;
import com.example.delivery.auth.dto.response.SigninResponseDto;
import com.example.delivery.auth.dto.response.SignupResponseDto;
import com.example.delivery.auth.entity.AuthCredential;
import com.example.delivery.auth.repository.AuthCredentialRepository;
import com.example.delivery.global.security.JwtUtil;
import com.example.delivery.user.entity.User;
import com.example.delivery.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthCredentialRepository authCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    //signup
    @Transactional
    public SignupResponseDto signup(SignupRequestDto dto){
        if(authCredentialRepository.existsByLoginId(dto.getLoginId())){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 사용중인 아이디입니다."
            );
        }

        // 원본 비밀번호 해시화
        String passwordHash = passwordEncoder.encode(dto.getPassword());

        // 회원정보 저장
        User user = userRepository.save(new User(dto.getRole(), dto.getUsername()));

        // 저장된 회원정보와 credential 연결
        AuthCredential credential = new AuthCredential(user, dto.getLoginId(),passwordHash);

//        authCredentialRepository.saveAndFlush(credential);
//        // saveAndFlush 는 저장할 변경 사항을 DB에 즉시 반영하도록 함
//        // 동시에 같은 아이디로 가입하여 UNIQUE 제약을 위반하면 여기서 예외가 발생. 트랜잭션 롤백
        //위 코드 대신 동시 가입을 아래처럼 처리한다
        try{
            authCredentialRepository.saveAndFlush(credential);
        } catch (DataIntegrityViolationException e){
            if(isUniqueViolation(e)){
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "이미 사용중인 회원 정보와 중복됩니다."
                );
            }

            throw e;
        }


        return new SignupResponseDto(
                user.getId(),
                credential.getLoginId(),
                user.getUsername(),
                user.getRole()
        );
    }

    @Transactional
    public SigninResponseDto signin(SigninRequestDto dto) {
        AuthCredential credential =
                authCredentialRepository.findByLoginId(dto.getLoginId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "로그인 정보가 올바르지 않습니다."
                        ));

        boolean matched = passwordEncoder.matches(dto.getPassword(), credential.getPasswordHash());

        if(!matched){
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "로그인 정보가 올바르지 않습니다."
            );
        }

        User user = credential.getUser();

        String accessToken = jwtUtil.createAccessToken(
                user.getId(),
                credential.getLoginId(),
                user.getRole()
        );

        return new SigninResponseDto(accessToken);
    }

    //단순 로직이지만 현재는 여기서만 쓰이고, 짧으므로 서비스 내에 계속 위치시키기
    private boolean isUniqueViolation(Throwable exception) {
        Throwable cause = exception;

        while (cause != null) {
            if (cause instanceof SQLException sqlException
                    && "23505".equals(sqlException.getSQLState())) {
                return true;
            }

            cause = cause.getCause();
        }

        return false;
    }
}
