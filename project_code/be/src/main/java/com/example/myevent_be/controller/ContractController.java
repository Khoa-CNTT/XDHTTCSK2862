package com.example.myevent_be.controller;

import com.example.myevent_be.dto.request.ContractRequest;
import com.example.myevent_be.dto.request.ContractUpdateRequest;
import com.example.myevent_be.dto.response.ApiResponse;
import com.example.myevent_be.dto.response.ContractResponse;
import com.example.myevent_be.entity.Contract;
import com.example.myevent_be.exception.AppException;
import com.example.myevent_be.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {
    private final ContractService contractService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ApiResponse<ContractResponse> createContract(@RequestBody @Valid ContractRequest contractRequest) {
        ContractResponse contractResponse = contractService.createContract(contractRequest);
        return ApiResponse.<ContractResponse>builder()
                .result(contractResponse)
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{contractId}")
    public ApiResponse<ContractResponse> getContractById(@PathVariable String contractId) {
        ContractResponse contractResponse = contractService.getContractById(contractId);
        return ApiResponse.<ContractResponse>builder()
                .result(contractResponse)
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{contractId}")
    public ApiResponse<ContractResponse> updateContract(@PathVariable String contractId, @RequestBody @Valid ContractUpdateRequest contractUpdateRequest) {
        ContractResponse contractResponse = contractService.updateContract(contractId, contractUpdateRequest);
        return ApiResponse.<ContractResponse>builder()
                .result(contractResponse)
                .build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{contractId}")
    public ApiResponse<Void> deleteContract(@PathVariable String contractId) {
        contractService.deleteContract(contractId);
        return ApiResponse.<Void>builder()
                .result(null)
                .build();
    }
}
