package com.example.myevent_be.repository;

import com.example.myevent_be.entity.User;
import com.example.myevent_be.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Collection;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail(String email);

    //    @EntityGraph(attributePaths = "role") // Load luôn roles khi lấy user
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(String email);
    List<User> findByRoleIn(Collection<Role> roles);

}
