package com.example.myevent_be.exception;

import com.example.myevent_be.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeExceptioin(Exception exception){
        exception.printStackTrace(); // Log lỗi để debug
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
        ApiResponse apiResponse = ApiResponse.builder()
                .code(HttpStatus.NOT_FOUND.value())  // HTTP 404
                .message(exception.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception){
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatus()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDeniedException(AccessDeniedException exception){
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        return ResponseEntity.status(errorCode.getStatus()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidattion(MethodArgumentNotValidException exception){
//        String enumkey = exception.getFieldError().getDefaultMessage();
        String enumkey = exception.getFieldError() != null ? exception.getFieldError().getDefaultMessage() : "INVALID_INPUT";

        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attribute = null;
        try {
            errorCode = ErrorCode.valueOf(enumkey);

            var constrainViolation = exception.getBindingResult().getAllErrors().stream()
                    .findFirst()
                    .map(error -> ((ConstraintViolation<?>) error.unwrap(ConstraintViolation.class)))
                    .orElse(null);
            if (constrainViolation != null){
                attribute = constrainViolation.getConstraintDescriptor().getAttributes();
            }
        }
        catch (IllegalArgumentException e){
        }
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        apiResponse.setMessage(
                Objects.nonNull(attribute) ? mapAttribute(errorCode.getMessage(), attribute) : errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    private String mapAttribute(String message, Map<String, Object> attribute){
        String minValue= String.valueOf(attribute.get(MIN_ATTRIBUTE));

        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }

}
