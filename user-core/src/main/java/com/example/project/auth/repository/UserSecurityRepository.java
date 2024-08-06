package com.example.project.auth.repository;

import com.example.project.auth.entity.UserSecurity;
import com.example.project.common.enums.LoginProvider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSecurityRepository extends JpaRepository<UserSecurity, Long> {

    @EntityGraph(attributePaths = {"user", "user.authorities"})
    Optional<UserSecurity> findBySocialMemberIdAndProvider(String socialMemberId, LoginProvider provider);

    Optional<UserSecurity> findByEmailAndProvider(String email, LoginProvider loginProvider);

    Optional<UserSecurity> findByEmail(String email);
}
