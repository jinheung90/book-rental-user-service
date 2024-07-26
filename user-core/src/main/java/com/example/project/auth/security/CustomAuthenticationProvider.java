package com.example.project.auth.security;



import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationProvider {

    public CustomUserDetail setAuthentication(Long userId, String email, List<String> authorities) {

        List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream().map(SimpleGrantedAuthority::new).toList();

        CustomUserDetail userDetail = CustomUserDetail
                .builder()
                .pk(userId)
                .authorities(grantedAuthorities)
                .email(email)
                .password("")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetail, null, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return (CustomUserDetail) authentication.getPrincipal();
    }

}
