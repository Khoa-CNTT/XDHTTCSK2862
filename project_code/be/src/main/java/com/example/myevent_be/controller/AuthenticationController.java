package com.example.myevent_be.controller;

import com.example.myevent_be.dto.request.*;
import com.example.myevent_be.dto.response.ApiResponse;
import com.example.myevent_be.dto.response.AuthenticationResponse;
import com.example.myevent_be.dto.response.IntrospectResponse;
import com.example.myevent_be.dto.response.UserResponse;
import com.example.myevent_be.entity.User;
import com.example.myevent_be.service.AuthenticationService;
import com.example.myevent_be.service.PasswordResetService;
import com.example.myevent_be.service.UserVerificationService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    PasswordResetService passwordResetService;
    UserVerificationService userVerificationService;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticated(@RequestBody AuthenticationRequest request){
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/forgot-password")
    ApiResponse<UserResponse> forgotPassword(@Valid @RequestBody FogetPasswordRequest request) {
        var result = passwordResetService.sendResetPasswordToken(request);
        return ApiResponse.<UserResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/reset-password")
    ApiResponse<UserResponse> resetPassword(
            @RequestParam String token,
            @Valid @RequestBody ResetPasswordRequest request) {
        var result = passwordResetService.resetPassword(token, request);
        return ApiResponse.<UserResponse>builder()
                .result(result)
                .build();
    }

    @GetMapping("/verify-code")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> verifyEmail(@RequestParam String code) {
        if (userVerificationService.verifyCode(code)) {
            // Giả sử bạn có thể lấy user qua email liên quan tới code,
            // hoặc hiện tại bạn chưa cần trả token/user sau khi xác thực.
            return ResponseEntity.ok(ApiResponse.<AuthenticationResponse>builder()
                    .message("Xác thực thành công")
                    .build());
        }

        return ResponseEntity.badRequest()
                .body(ApiResponse.<AuthenticationResponse>builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .message("Mã xác thực không hợp lệ hoặc đã hết hạn")
                        .build());
    }

}
