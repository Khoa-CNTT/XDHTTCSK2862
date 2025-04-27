package com.example.myevent_be.mapper;

import com.example.myevent_be.enums.ContractStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface StatusMapper {
    @Named("statusToString")
    default String statusToString(ContractStatus status) {
        return status != null ? status.name() : ContractStatus.DRAFT.name();
    }

    @Named("stringToStatus")
    default ContractStatus stringToStatus(String status) {
        return status != null ? ContractStatus.valueOf(status) : ContractStatus.DRAFT;
    }
}