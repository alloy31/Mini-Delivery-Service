package com.example.delivery.global.config;

import com.example.delivery.global.security.JwtAuthenticationFilter;
import com.example.delivery.global.security.JwtUtil;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtUtil jwtUtil)
        throws Exception{

        http
                .csrf(AbstractHttpConfigurer::disable) //인증 쿠키를 사용하지 않음으로 csrf 비활성
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //세션이 인증 정보를 관리하는 걸 금지
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll() //내부에서 오류로 응답처리를 하는 걸 허용
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/signup",
                                "/api/v1/auth/login"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/menus",
                                "/api/v1/menus/{menuId}"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, error) ->
                                response.setStatus(
                                        HttpServletResponse.SC_UNAUTHORIZED
                                )
                        )
                        .accessDeniedHandler((request, response, error) ->
                                response.setStatus(
                                        HttpServletResponse.SC_FORBIDDEN
                                )
                        )
                );

        //JWT 필터를 Security 필터 체인에 등록한다.
        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtUtil),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

}
