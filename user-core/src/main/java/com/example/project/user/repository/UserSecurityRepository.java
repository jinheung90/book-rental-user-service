package com.example.project.user.repository;

import com.example.project.user.entity.UserSecurity;
import com.example.project.common.enums.LoginProvider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSecurityRepository extends JpaRepository<UserSecurity, Long> {

    @EntityGraph(attributePaths = {"user", "user.authorities", "user.userProfile"})
    Optional<UserSecurity> findBySocialMemberIdAndProvider(String socialMemberId, LoginProvider provider);

    @EntityGraph(attributePaths = {"user", "user.authorities", "user.userProfile"})
    Optional<UserSecurity> findByEmailAndProvider(String email, LoginProvider loginProvider);

    Optional<UserSecurity> findByEmail(String email);
}
