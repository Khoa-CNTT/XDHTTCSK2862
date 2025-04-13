package com.example.myevent_be.service;

import com.example.myevent_be.dto.request.ContractRequest;
import com.example.myevent_be.dto.request.ContractUpdateRequest;
import com.example.myevent_be.dto.response.ContractResponse;
import com.example.myevent_be.entity.Contract;
import com.example.myevent_be.exception.AppException;
import com.example.myevent_be.exception.ErrorCode;
import com.example.myevent_be.mapper.ContractMapper;
import com.example.myevent_be.repository.ContractRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractService {
    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;

    @Transactional
    public ContractResponse createContract(ContractRequest contractRequest) {
        log.info("Creating contract with payment intent id: {}", contractRequest.getPaymentIntentId());
        
        // Kiểm tra contract đã tồn tại chưa
        UUID paymentIntentId = contractRequest.getPaymentIntentId();
        contractRepository.findByPaymentIntentId(paymentIntentId)
                .ifPresent(contract -> {
                    log.warn("Contract already exists with payment intent id: {}", paymentIntentId);
                    throw new AppException(ErrorCode.CONTRACT_ALREADY_EXISTED);
                });

        // Tạo contract mới
        Contract contract = contractMapper.toContract(contractRequest);
        Contract savedContract = contractRepository.save(contract);
        log.info("Created contract with id: {}", savedContract.getId());
        
        return contractMapper.toContractResponse(savedContract);
    }

    public List<ContractResponse> getContracts() {
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        if (role.equals("ADMIN")) {
            return contractRepository.findAll().stream()
                    .map(contractMapper::toContractResponse)
                    .toList();
        } else {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            return contractRepository.findByCustomerId(userId).stream()
                    .map(contractMapper::toContractResponse)
                    .toList();
        }
    }

    public ContractResponse getContractById(String contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        if (!role.equals("ADMIN")) {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!contract.getCustomer().getId().equals(userId)) {
                throw new AppException(ErrorCode.CONTRACT_NOT_FOUND);
            }
        }

        return contractMapper.toContractResponse(contract);
    }

    @Transactional
    public ContractResponse updateContract(String contractId, ContractUpdateRequest contractUpdateRequest) {
        Contract existingContract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
        contractMapper.updateContract(existingContract, contractUpdateRequest);
        Contract updatedContract = contractRepository.save(existingContract);
        return contractMapper.toContractResponse(updatedContract);
    }

    @Transactional
    public void deleteContract(String contractId) {
        if (!contractRepository.existsById(contractId)) {
            throw new AppException(ErrorCode.CONTRACT_NOT_FOUND);
        }
        contractRepository.deleteById(contractId);
    }

    public ContractResponse getContractByPaymentIntentId(UUID paymentIntentId) {
        return contractRepository.findByPaymentIntentId(paymentIntentId)
                .map(contractMapper::toContractResponse)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
    }
}
