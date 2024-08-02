package com.example.project.auth.repository;

import com.example.project.auth.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"authorities", "userSecurities"})
    Optional<User> findOneWithAuthoritiesAndUserSecuritiesByEmail(String email);
    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByEmail(String email);
    Boolean existsByIdAndDeleted(Long userId, Integer deleted);
    long countByDeleted(Integer deleted);

    boolean existsByPhone(String phone);
}
