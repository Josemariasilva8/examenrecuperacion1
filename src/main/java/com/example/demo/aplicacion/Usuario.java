package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String username;

    @Column(unique = true, nullable = false, length = 90)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "roles_rol_id", nullable = false)
    private Rol rol;

    @ManyToMany
    @JoinTable(
        name = "users_follows_users",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "user_to_follow_id")
    )
    private Set<Usuario> seguidores = new HashSet<>();
    @ManyToMany(mappedBy = "seguidores")
private Set<Usuario> seguidos = new HashSet<>();

}
