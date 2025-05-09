package com.example.myevent_be.service;

import com.example.myevent_be.dto.request.FogetPasswordRequest;
import com.example.myevent_be.dto.request.ResetPasswordRequest;
import com.example.myevent_be.dto.response.UserResponse;
import com.example.myevent_be.entity.PasswordResetToken;
import com.example.myevent_be.entity.User;
import com.example.myevent_be.exception.AppException;
import com.example.myevent_be.exception.ErrorCode;
import com.example.myevent_be.mapper.UserMapper;
import com.example.myevent_be.repository.PasswordResetTokenRepository;
import com.example.myevent_be.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final UserMapper userMapper;

    @Transactional
    public UserResponse sendResetPasswordToken(FogetPasswordRequest request) {
        log.info("Starting password reset process for email: {}", request.getEmail());
        try {
            // 1. Kiểm tra email có tồn tại không
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
            log.info("Found user with email: {}", user.getEmail());

            // 2. Xóa token cũ nếu có
            tokenRepository.deleteByUser(user);
            log.info("Deleted any existing tokens for user");

            // 3. Tạo token reset mới
            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUser(user);
            resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
            
            // 4. Lưu token mới
            PasswordResetToken savedToken = tokenRepository.save(resetToken);
            log.info("Saved new reset token: {}", savedToken.getToken());
            
            // 5. Gửi email
            String resetLink = "https://your-frontend.com/reset-password?token=" + token;
            String body = """
                Xin chào %s,
                
                Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng nhấn vào liên kết sau để đặt lại mật khẩu (có hiệu lực trong 15 phút):

                %s

                Nếu bạn không yêu cầu, hãy bỏ qua email này.
                """.formatted(user.getEmail(), resetLink);

            emailService.send(user.getEmail(), "Yêu cầu đặt lại mật khẩu", body);
            log.info("Sent reset password email to: {}", user.getEmail());
            
            return userMapper.toUserResponse(user);
        } catch (Exception e) {
            log.error("Error during password reset process: ", e);
            throw e;
        }
    }

    @Transactional
    public UserResponse resetPassword(String token, ResetPasswordRequest request) {
        log.info("Starting password reset with token: {}", token);
        try {
            // 1. Kiểm tra token có tồn tại và chưa hết hạn
            PasswordResetToken resetToken = tokenRepository.findByToken(token)
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));
            log.info("Found valid reset token for user: {}", resetToken.getUser().getEmail());

            if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                tokenRepository.delete(resetToken);
                throw new AppException(ErrorCode.TOKEN_EXPIRED);
            }

            // 2. Kiểm tra mật khẩu mới và xác nhận mật khẩu
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new AppException(ErrorCode.PASSWORD_MISMATCH);
            }

            // 3. Cập nhật mật khẩu mới
            User user = resetToken.getUser();
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            log.info("Updated password for user: {}", user.getEmail());

            // 4. Xóa token đã sử dụng
            tokenRepository.delete(resetToken);
            log.info("Deleted used reset token");

            return userMapper.toUserResponse(user);
        } catch (Exception e) {
            log.error("Error during password reset: ", e);
            throw e;
        }
    }
}
