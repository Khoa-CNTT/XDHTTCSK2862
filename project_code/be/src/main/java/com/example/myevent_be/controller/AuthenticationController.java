package com.example.myevent_be.controller;

import com.example.myevent_be.dto.request.AuthenticationRequest;
import com.example.myevent_be.dto.request.ForgetPasswordRequest;
import com.example.myevent_be.dto.request.LogoutRequest;
import com.example.myevent_be.dto.request.ResetPasswordRequest;
import com.example.myevent_be.dto.response.ApiResponse;
import com.example.myevent_be.dto.response.AuthenticationResponse;
import com.example.myevent_be.service.AuthenticationService;
import com.example.myevent_be.service.PasswordResetService;
import com.example.myevent_be.service.UserVerificationService;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@CrossOrigin(origins = "http://127.0.0.1:5500")
public class AuthenticationController {
    AuthenticationService authenticationService;
    PasswordResetService passwordResetService;
    UserVerificationService userVerificationService;
    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> authenticated(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgetPasswordRequest request)
            throws MessagingException {
        passwordResetService.sendResetPasswordToken(request);
        return ApiResponse.<Void>builder()
                .message("Mã xác thực đã được gửi đến email của bạn")
                .build();
    }

    @PostMapping("/verify-pass-code")
    public ResponseEntity<String> verifyCode(@RequestBody ForgetPasswordRequest request) {
        boolean isValid = passwordResetService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(isValid ? "Mã đúng" : "Mã sai");
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getNewPassword());
        return ApiResponse.<Void>builder()
                .message("Đặt lại mật khẩu thành công")
                .build();
    }

    @GetMapping("/verify-code")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> verifyEmail(@RequestParam String code) {
        if (userVerificationService.verifyCode(code)) {
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
