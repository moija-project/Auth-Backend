package com.example.loginservice.etity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;


@Setter
@Getter
public class Account extends User {
    private String nickname;
    private String uuid;

    public Account(String username, String password, Collection<GrantedAuthority> authorities, String nickname, String uuid) {
        super(username,password,authorities);
        this.nickname = nickname;
        this.uuid = uuid;
    }
}
