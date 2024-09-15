package com.example.project.user.repository;

import com.example.project.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"authorities", "userSecurities"})
    Optional<User> findOneWithAuthoritiesAndUserSecuritiesByEmail(String email);

    @EntityGraph(attributePaths = {"authorities", "userSecurities"})
    Optional<User> findOneWithAuthoritiesAndUserSecuritiesByPhone(String phone);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByEmail(String email);

    boolean existsByPhone(String phone);

    @EntityGraph(attributePaths = {"authorities", "userSecurities", "userProfile"})
    List<User> findByIdIn(List<Long> ids);

    boolean existsByEmail(String email);
}
