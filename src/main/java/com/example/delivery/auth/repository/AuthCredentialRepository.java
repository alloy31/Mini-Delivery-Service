package com.example.delivery.auth.repository;

import com.example.delivery.auth.entity.AuthCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//Spring Data JPA가 인터페이스의 구현체와 메서드 이름에 따른 조회를 제공하므로, 직접 구현 클래스를 만들 필요는 없다.
public interface AuthCredentialRepository extends JpaRepository<AuthCredential, Long> {
    boolean existsByLoginId(String loginId);

    Optional<AuthCredential> findByLoginId(String loginId);
}
