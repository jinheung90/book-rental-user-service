package com.example.project.auth.repository;


import com.example.project.auth.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    boolean existsByAuthorityName(String role);
}
