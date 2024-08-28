package com.example.project.address.repository;

import com.example.project.address.entity.RoadAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoadAddressRepository extends JpaRepository<RoadAddress, Long> {
}
