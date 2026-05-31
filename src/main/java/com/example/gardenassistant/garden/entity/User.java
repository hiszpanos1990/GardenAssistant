package com.example.gardenassistant.garden.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true , nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(unique = true , nullable = false)
    private String email;
    @Enumerated(EnumType.STRING)
    private Roles role;
    @Column(length = 500)
    private String refreshToken;

    @OneToMany(mappedBy = "user")
    private List<Garden> gardens = new ArrayList<>();
}
