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

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ContractMapper {
    @Mapping(source = "status", target = "status", qualifiedByName = "mapStatus")
    @Mapping(source = "paymentIntentId", target = "paymentIntentId", qualifiedByName = "mapUUID")
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

    @Named("mapStatusToString")
    default String mapStatusToString(ContractStatus status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }

    @Named("mapUUID")
    default UUID mapUUID(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return UUID.fromString(uuid.toString());
    }
}