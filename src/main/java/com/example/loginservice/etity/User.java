package com.example.loginservice.etity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
public class User{

    @Id
    @Column(name = "user_id", nullable = false)
    private String username;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "gender", nullable = false)
    private boolean gender;

    @DateTimeFormat
    @Column(name = "birth", nullable = false)
    private LocalDate birth;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "profile")
    private String profile;

    @Column(name = "time_join", nullable = false)
    private Timestamp timeJoin;

    @Column(name = "reliability_user", nullable = false, columnDefinition = "FLOAT DEFAULT 3")
    private float reliabilityUser;

    @Column(name = "authority")
    @Enumerated(EnumType.STRING)
    private Authority authority;

    /*여기부터 UserDetails*/

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled;
    @Column(name = "is_account_nonlocked", nullable = false)
    private boolean isAccountNonLocked;

}