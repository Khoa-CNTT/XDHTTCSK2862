package com.example.myevent_be.mapper;

import com.example.myevent_be.dto.request.UserCreateRequest;
import com.example.myevent_be.dto.request.UserUpdateRequest;
import com.example.myevent_be.dto.response.UserResponse;
import com.example.myevent_be.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {
    User toUser(UserCreateRequest request);

//    @Mapping(target = "roleName", source = "role")
    @Mapping(source = "role", target = "roleName", qualifiedByName = "roleToString")
    UserResponse toUserResponse(User user);

    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
