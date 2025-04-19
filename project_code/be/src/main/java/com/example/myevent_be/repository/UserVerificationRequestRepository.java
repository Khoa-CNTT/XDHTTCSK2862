package com.example.myevent_be.repository;

import com.example.myevent_be.entity.UserVerificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserVerificationRequestRepository extends JpaRepository<UserVerificationRequest, String > {
    Optional<UserVerificationRequest> findByCode(String code);
    void deleteByEmail(String email);
}
