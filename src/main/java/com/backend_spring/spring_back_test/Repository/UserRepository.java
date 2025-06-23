package com.backend_spring.spring_back_test.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend_spring.spring_back_test.Models.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}