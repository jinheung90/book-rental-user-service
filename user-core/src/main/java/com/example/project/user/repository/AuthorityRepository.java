package com.example.project.user.repository;


import com.example.project.user.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    boolean existsByAuthorityName(String role);
}
