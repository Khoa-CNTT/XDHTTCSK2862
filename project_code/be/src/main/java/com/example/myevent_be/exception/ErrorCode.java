package com.example.myevent_be.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INSUFFICIENT_STORAGE),
    INVALID_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "Unauthorized", HttpStatus.UNAUTHORIZED),
    EVENTTYPE_EXISTED(1008, "Event type existed", HttpStatus.BAD_REQUEST),
    EVENT_TYPE_NOT_FOUND(1009, "Event type not found", HttpStatus.BAD_REQUEST),
    CONTRACT_ALREADY_EXISTED(1010, "Contract already exists with this Payment Intent ID", HttpStatus.BAD_REQUEST),
    CONTRACT_NOT_FOUND(1011,"CONTRACT_NOT_FOUND",HttpStatus.BAD_REQUEST),
    EMAIL_NOT_FOUND(1012, "Email not found", HttpStatus.NOT_FOUND),
    INVALID_TOKEN(1013, "Invalid or expired reset token", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(1014, "Reset token has expired", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH(1015, "New password and confirm password do not match", HttpStatus.BAD_REQUEST)
    ;

    int code;
    String message;
    HttpStatus status;

    ErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
