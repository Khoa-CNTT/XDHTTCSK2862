package com.example.myevent_be.repository;

import com.example.myevent_be.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, String> {
    boolean existsByPaymentIntentId(String paymentIntentId);

}
