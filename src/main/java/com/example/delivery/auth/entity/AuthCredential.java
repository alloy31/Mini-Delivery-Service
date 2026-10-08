package com.example.delivery.auth.entity;

import com.example.delivery.global.entity.BaseEntity;
import com.example.delivery.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "auth_credentials")
public class AuthCredential extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String loginId;

    @Column(nullable = false)
    private String passwordHash;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    //JPA를 위한 기본 생성자
    protected AuthCredential(){}

    public AuthCredential(User user, String loginId, String passwordHash){
        this.user = user;
        this.loginId = loginId;
        this.passwordHash = passwordHash;
    }

}
