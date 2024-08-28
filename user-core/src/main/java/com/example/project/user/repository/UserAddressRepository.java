package com.example.project.user.repository;

import com.example.project.user.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    void deleteAllByUserId(Long userId);
}
