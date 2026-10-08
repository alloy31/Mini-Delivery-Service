package com.example.delivery.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "auth_credentials")
public class AuthCredential {
    @Id
    private Long id;
}
