package com.example.project.user.repository;


import com.example.project.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserProfileRepository  extends JpaRepository<UserProfile, Long> {
    List<UserProfile> findAllByUserIdIn(List<Long> ids);
}