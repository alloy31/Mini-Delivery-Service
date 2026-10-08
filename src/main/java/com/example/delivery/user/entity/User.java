package com.example.delivery.user.entity;

import com.example.delivery.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.CUSTOMER;

    private String username;

    protected User(){}
    // JPA가 DB 조회 결과로 엔티티를 만들 때 사용하는 기본 생성자
    // 다른 생성자를 직접 선언하면 자동 기본 생성자가 없어지므로 명시한다.

    // 필수 요소만 이용해서 생성해준다. 시간 등은 BaseEntity에서 작성해준다.
    public User(UserRole role, String username){
        this.role = role;
        this.username = username;
    }

}
