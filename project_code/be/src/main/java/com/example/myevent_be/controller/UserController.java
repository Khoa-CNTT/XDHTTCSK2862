package com.example.myevent_be.controller;

import com.example.myevent_be.dto.request.UpdateUserRoleRequest;
import com.example.myevent_be.dto.request.UserCreateRequest;
import com.example.myevent_be.dto.request.UserUpdateRequest;
import com.example.myevent_be.dto.response.ApiResponse;
import com.example.myevent_be.dto.response.UserResponse;
import com.example.myevent_be.entity.User;
import com.example.myevent_be.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Builder
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

//    @Autowired
    UserService userService;

    // dang ky tai khoan
    @PostMapping("/signing-up")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    List<UserResponse> getUsers(){
        return userService.getUsers();
    }


    @GetMapping("/{userId}")
    UserResponse getUser(@PathVariable("userId") String userId){
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    UserResponse updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return userService.updateUser(request, userId);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable String userId){
        userService.deleteUser(userId);
        return "User has been deleted";
    }

    @PutMapping("/update-role/{userId}")
    UserResponse updateUserRole(@PathVariable String userId,@RequestBody UpdateUserRoleRequest request) {
        return userService.updateUserRole(userId, request.getRole());
    }
}
