package com.example.delivery.menu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "menus")
public class Menu {
    @Id
    private Long id;
}
