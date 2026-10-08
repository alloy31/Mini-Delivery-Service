package com.example.delivery.auth.dto.request;

import com.example.delivery.user.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;

@Getter
@Setter
public class SignupRequestDto {

    @NotBlank(message = "아이디는 필수입니다.") // Note: NotNull은 공백도 허용한다.
    @Size(min =4, max = 20, message = "아이디는 4-20자 이내로 입력해주세요.")
    private String loginId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) //JSON 요청에서 값을 읽는 건 허용하지만, 출력할때는 password를 제한한다.
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상으로 사용해주세요.")
    private String password;

    @NotNull(message = "유저 역할을 지정해주세요.")
    private UserRole role;

    @Size(max = 255, message = "사용자 이름은 255자 이하여야 합니다.")
    private String username;

    @JsonIgnore
    @AssertTrue(message = "비밀번호는 UTF-8 기준 72바이트 이하여야 합니다.")
    public boolean isPasswordWithinBcryptLimit() {
        return password == null || password.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
}
