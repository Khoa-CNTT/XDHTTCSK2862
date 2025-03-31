package com.example.myevent_be.service;

import com.example.myevent_be.dto.request.UpdateUserRoleRequest;
import com.example.myevent_be.dto.request.UserCreateRequest;
import com.example.myevent_be.dto.request.UserUpdateRequest;
import com.example.myevent_be.dto.response.UserResponse;
import com.example.myevent_be.entity.Role;
import com.example.myevent_be.entity.User;
import com.example.myevent_be.exception.AppException;
import com.example.myevent_be.exception.ErrorCode;
import com.example.myevent_be.mapper.UserMapper;
import com.example.myevent_be.repository.RoleRepository;
import com.example.myevent_be.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreateRequest request){

        if (userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

//        // Lấy Role từ cơ sở dữ liệu
//        Role role = roleRepository.findByName("USER")
//                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));
//
//        // Gán Role cho User
//        user.setRole(role);

//        // Gán Role mặc định mà không lấy từ database
//        Role defaultRole = new Role();
//        defaultRole.setName("USER");
//
//        user.setRole(defaultRole);

        // Kiểm tra xem role "USER" đã tồn tại chưa
        Role role = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("USER");
                    return roleRepository.save(newRole); // Lưu vào database nếu chưa có
                });

        // Gán role cho user
        user.setRole(role);
//        user.setRole(Set.of(role));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserResponse> getUsers(){
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponse getUser(String id){
        log.info("Fetching user with ID:  {}", id);
        return userMapper.toUserResponse(userRepository
                .findById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    public UserResponse updateUser(UserUpdateRequest request, String userId){

        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        userMapper.updateUser(user, request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));


        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public UserResponse updateUserRole(String userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        user.setRole(role);
//        user.setRole(Set.of(role));

        return userMapper.toUserResponse(userRepository.save(user));
    }
}
