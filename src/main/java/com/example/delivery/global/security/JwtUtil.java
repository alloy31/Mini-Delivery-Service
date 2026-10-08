package com.example.delivery.global.security;

import com.example.delivery.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    // 발급자 정보
    private final String issuer;

    // 토큰 유효 기간
    private final long expirationSeconds;

    // 설정을 마친 JwtParser는 여러 요청에서 재사용할 수 있다.
    private final JwtParser parser;

    public JwtUtil(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.issuer}") String issuer,
            @Value("${jwt.access-token-expiration-seconds}")
            long expirationSeconds
    ) {
        if (issuer.isBlank() || expirationSeconds <= 0) {
            throw new IllegalArgumentException(
                    "JWT 설정값이 잘못되었습니다."
            );
        }

        this.secretKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secretKey)
        );

        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;

        this.parser = Jwts.parser() // 서명 검증 조건 설정
                .verifyWith(this.secretKey)
                .requireIssuer(issuer)
                .sig().clear().add(Jwts.SIG.HS256).and()
                .build();
    }

    // access token 발급
    public String createAccessToken(
            Long userId,
            String loginId,
            UserRole role
    ) {
        // 서비스에서 전달받은 토큰 발급용 사용자 정보가 유효한지 검증
        if (userId == null || userId <= 0
                || loginId == null || loginId.isBlank()
                || role == null) {
            throw new IllegalArgumentException(
                    "토큰 발급 정보가 올바르지 않습니다."
            );
        }

        Instant now = Instant.now();

        return Jwts.builder()
                .issuer(issuer)
                .subject(userId.toString())
                .claim("loginId", loginId)
                .claim("role", role.name())
                .issuedAt(Date.from(now))
                .expiration(
                        Date.from(now.plusSeconds(expirationSeconds))
                )
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact(); // 최종 JWT 문자열을 생성한다.
    }

    // 토큰 검증. 서명 불일치 등의 기본적인 예외는 jjwt가 알려줌
    public JwtPrincipal parseAccessToken(String token) {
        Claims claims = parser.parseSignedClaims(token).getPayload();

        String subject = claims.getSubject();
        String loginId = claims.get("loginId", String.class);
        String roleName = claims.get("role", String.class);


        if (subject == null
                || loginId == null || loginId.isBlank()
                || roleName == null
                || claims.getExpiration() == null) {
            throw new JwtException(
                    "토큰에 필수 정보가 없습니다."
            );
        }

        try {
            // sub는 문자열이므로 DB의 PK 타입인 Long으로 변환한다.
            Long userId = Long.valueOf(subject);

            // CUSTOMER 또는 OWNER에 해당하지 않으면 예외가 발생한다.
            UserRole role = UserRole.valueOf(roleName);

            if (userId <= 0) {
                throw new IllegalArgumentException(
                        "잘못된 사용자 ID"
                );
            }

            return new JwtPrincipal(userId, loginId, role);

        } catch (IllegalArgumentException e) {
            throw new JwtException(
                    "토큰의 사용자 정보가 올바르지 않습니다.",
                    e
            );
        }
    }
}