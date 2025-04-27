package com.example.myevent_be.mapper;

import com.example.myevent_be.dto.request.ContractRequest;
import com.example.myevent_be.dto.request.ContractUpdateRequest;
import com.example.myevent_be.dto.response.ContractResponse;
import com.example.myevent_be.entity.Contract;
import com.example.myevent_be.enums.ContractStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ContractMapper {
    Contract toContract(ContractRequest request);
    
    @Mapping(source = "create_at", target = "createdAt")
    @Mapping(source = "update_at", target = "updatedAt")
    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "customer.phone_number", target = "customerPhone")
    @Mapping(source = "customer.address", target = "eventAddress")
    @Mapping(source = "rental.id", target = "rentalId")
    ContractResponse toContractResponse(Contract contract);
    
    void updateContract(@MappingTarget Contract contract, ContractUpdateRequest contractUpdateRequest);

    @Named("mapStatus")
    default ContractStatus mapStatus(String status) {
        if (status == null) {
            return null;
        }
        return ContractStatus.valueOf(status);
    }
}
