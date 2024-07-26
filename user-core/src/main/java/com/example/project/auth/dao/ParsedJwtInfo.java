package com.example.project.auth.dao;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
public class ParsedJwtInfo {
    private Long userId;
    private List<GrantedAuthority> authorities;
    private Date expiration;
}
