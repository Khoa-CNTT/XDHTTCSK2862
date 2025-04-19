package com.example.myevent_be.controller;

import com.example.myevent_be.dto.request.*;
import com.example.myevent_be.dto.response.ApiResponse;
import com.example.myevent_be.dto.response.UserResponse;
import com.example.myevent_be.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
//    private final IStorageService storageService;

    @PostMapping("/signing-up")
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/manager")
    @PreAuthorize("hasAuthority('MANAGER')")
    public List<UserResponse> getUserByRole(){
        return userService.getUserByRole();
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable("userId") String userId) {
        return userService.getUser(userId);
    }

    @PatchMapping(value = "/{userId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
//    ApiResponse<UserResponse> updateUser(
//            @PathVariable String userId,
//            @RequestPart(value = "avatar", required = false) MultipartFile file,
//            @RequestPart(value = "data", required = false) String data) {
//
//        UserUpdateRequest request = new UserUpdateRequest();
//        if (data != null) {
//            try {
//                ObjectMapper objectMapper = new ObjectMapper();
//                request = objectMapper.readValue(data, UserUpdateRequest.class);
//                log.info("Parsed update data: {}", data);
//            } catch (Exception e) {
//                log.error("Error parsing update data", e);
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ: " + e.getMessage());
//            }
//        }
//
//        try {
//            if (file != null && !file.isEmpty()) {
//                // Kiểm tra loại file
//                String contentType = file.getContentType();
//                if (contentType == null || !contentType.startsWith("image/")) {
//                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ hỗ trợ file ảnh (jpg, png, ...)");
//                }
//
//                // Kiểm tra kích thước
//                if (file.getSize() > 5 * 1024 * 1024) {
//                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File ảnh quá lớn, tối đa 5MB");
//                }
//
//                // Store the uploaded file
//                String fileName = storageService.storeFile(file);
//                log.info("File stored successfully with name: {}", fileName);
//
//                // Set the image path in the request - chỉ lưu tên file
//                request.setAvatar(fileName);
//            }
//
//            UserResponse response = userService.updateUser(userId, request);
//            // Đảm bảo response có URL đầy đủ của avatar
//            if (response != null && response.getAvatar() != null && !response.getAvatar().startsWith("/api/v1/FileUpload/files/")) {
//                response.setAvatar("/api/v1/FileUpload/files/" + response.getAvatar());
//            }
//            log.info("User updated successfully: {}", response);
//            return ApiResponse.<UserResponse>builder()
//                    .result(response)
//                    .build();
//        } catch (Exception e) {
//            log.error("Error updating user: ", e);
//            if (e instanceof AppException) {
//                throw e;
//            }
//            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
//        }
//    }
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            @RequestParam(value = "data", required = false) String data)
            throws IOException {
        UserResponse response = userService.updateUser(id, avatar, data);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return "User has been deleted";
    }

    @PutMapping("/update-role/{userId}")
    public UserResponse updateUserRole(@PathVariable String userId, @RequestBody UpdateUserRoleRequest request) {
        return userService.updateUserRole(userId, request.getRole());
    }

    @PutMapping("/update-password/{userId}")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @PathVariable("userId") String userId,
            @RequestBody ResetPasswordRequest2 request,
            @AuthenticationPrincipal Jwt jwt) {
        System.out.println("Received request with userId: " + userId);
        System.out.println("Old password: " + request.getOldPassword());
        System.out.println("New password: " + request.getNewPassword());
        System.out.println("Confirm password: " + request.getConfirmPassword());

        userService.resetPassword(userId, request);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .result("Password updated successfully")
                .build());
    }
}
