package com.example.myevent_be.repository;

import com.example.myevent_be.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

//    @EntityGraph(attributePaths = "role") // Load luôn roles khi lấy user
    Optional<User> findByEmail(String email);

//    Optional<User> findById(String useId);
}
