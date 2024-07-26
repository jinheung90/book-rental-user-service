package com.example.project.auth.security;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomUserDetail implements UserDetails {

    private List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    private String email;
    private String password;
    private Long pk;

    public Long getPK() {
        return this.pk;
    }

    public String getEmail() {return this.email; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.pk.toString();
    }
}
