package com.example.project.auth.service;


import com.example.project.auth.entity.Authority;
import com.example.project.auth.entity.User;

import com.example.project.auth.entity.UserSecurity;
import com.example.project.enums.LoginProvider;
import com.example.project.auth.repository.UserRepository;
import com.example.project.auth.repository.UserSecurityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserSecurityRepository userSecurityRepository;

    @Transactional(readOnly = true)
    public Optional<UserSecurity> findUserBySocialLogin(String socialId, LoginProvider loginProvider) {
        return userSecurityRepository.findBySocialMemberIdAndProvider(socialId, loginProvider);
    }

    @Transactional
    public UserSecurity signIn(String email, String socialMemberId, LoginProvider loginProvider) {
        User user = saveUser(email);
        return saveUserSecurity(user, email, socialMemberId, loginProvider);
    }

    public User saveUser(String email) {
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority("ROLE_USER"));
        final User user = User.builder()
                .authorities(authorities)
                .email(email)
                .starRating(0)
                .build();
        return userRepository.save(user);
    }

    public UserSecurity saveUserSecurity(User user, String email, String socialMemberId, LoginProvider provider) {
        final UserSecurity userSecurity
                = UserSecurity.builder()
                .user(user)
                .email(email)
                .password("empty")
                .provider(provider)
                .socialMemberId(socialMemberId)
                .build();
         return userSecurityRepository.save(userSecurity);
    }

}
