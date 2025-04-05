package com.example.myevent_be.service;

import com.example.myevent_be.dto.request.ContractRequest;
import com.example.myevent_be.dto.request.ContractUpdateRequest;
import com.example.myevent_be.dto.response.ContractResponse;
import com.example.myevent_be.entity.Contract;
import com.example.myevent_be.exception.AppException;
import com.example.myevent_be.exception.ErrorCode;
import com.example.myevent_be.mapper.ContractMapper;
import com.example.myevent_be.repository.ContractRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ContractService {
    ContractRepository contractRepository;
    ContractMapper contractMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public ContractResponse createContract(ContractRequest contractRequest) {
        String paymentIntentIdStr = contractRequest.getPaymentIntentId().toString();
        if (contractRepository.existsByPaymentIntentId(paymentIntentIdStr)) {
            throw new AppException(ErrorCode.CONTRACT_ALREADY_EXISTED);
        }
        Contract contract = contractMapper.toContract(contractRequest);
        Contract savedContract = contractRepository.save(contract);
        return contractMapper.toContractResponse(savedContract);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<ContractResponse> getContracts() {
        return contractRepository.findAll().stream()
                .map(contractMapper::toContractResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ContractResponse getContractById(String contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));

        return contractMapper.toContractResponse(contract);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ContractResponse updateContract(String contractId, ContractUpdateRequest contractUpdateRequest) {
        Contract existingContract = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ErrorCode.CONTRACT_NOT_FOUND));
        contractMapper.updateContract(existingContract, contractUpdateRequest);
        contractRepository.save(existingContract);
        return contractMapper.toContractResponse(existingContract);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteContract(String contractId) {
        if (!contractRepository.existsById(contractId)) {
            throw new AppException(ErrorCode.CONTRACT_NOT_FOUND);
        }

        contractRepository.deleteById(contractId);
    }
}
