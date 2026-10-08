package com.example.delivery.global.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null
                || !header.regionMatches(true, 0, "Bearer ", 0, 7)) {
            filterChain.doFilter(request, response);
            return;
        } //토큰이 없으면 인증정보만 만들지 않는다. 오류를 반환하지 않아 비로그인을 통해 접근하는 경로에 대해서도 접근 가능하게 한다.

        // "Bearer "는 7글자다. 접두사를 제거하고 JWT 문자열만 추출한다.
        String token = header.substring(7).trim();

        try {
            // 검증에 실패하면 예외가 발생하므로 아래 인증 등록까지 도달하지 않는다.
            JwtPrincipal principal = jwtUtil.parseAccessToken(token);

            // Spring Security의 hasRole("OWNER")는 ROLE_OWNER 권한을 찾는다 따라서 우리 enum 값을 Spring Security의 권한 표현으로 변환한다
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                    "ROLE_" + principal.role().name()
            );

            //클래스 이름에 UsernamePassword가 있지만,여기서 비밀번호를 검사하는 것은 아니다.
            UsernamePasswordAuthenticationToken authentication =
                    UsernamePasswordAuthenticationToken.authenticated(
                            principal, // 검증을 마친 사용자 정보
                            null, // 자격증명은 검증 이후이므로 null 값으로
                            List.of(authority) // 사용자에게 부여할 권한 목록
                    );

            SecurityContext context = //인증정보를 보관
                    SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

        } catch (JwtException | IllegalArgumentException e) {

            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}