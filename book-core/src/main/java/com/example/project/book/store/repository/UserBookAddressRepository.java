package com.example.project.book.store.repository;

import com.example.project.book.store.entity.UserBookAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBookAddressRepository extends JpaRepository<UserBookAddress, Long> {
}
