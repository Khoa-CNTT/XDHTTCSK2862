package com.example.myevent_be.service;

import com.example.myevent_be.dto.request.ForgetPasswordRequest;
import com.example.myevent_be.dto.request.ResetPasswordRequest;
import com.example.myevent_be.dto.response.UserResponse;
import com.example.myevent_be.entity.PasswordResetToken;
import com.example.myevent_be.entity.User;
import com.example.myevent_be.exception.AppException;
import com.example.myevent_be.exception.ErrorCode;
import com.example.myevent_be.mapper.UserMapper;
import com.example.myevent_be.repository.PasswordResetTokenRepository;
import com.example.myevent_be.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PasswordResetService {
    UserRepository userRepository;
    PasswordResetTokenRepository passwordResetTokenRepository;
    JavaMailSender mailSender;
    UserMapper userMapper;

    // ✅ Biến lưu mã xác nhận đã gửi (key = email, value = mã)
    private final Map<String, PasswordResetToken> verificationCodes = new ConcurrentHashMap<>();

    @Transactional
    public UserResponse sendResetPasswordToken(ForgetPasswordRequest request)
            throws MessagingException {
        log.info("Starting password reset process for email: {}", request.getEmail());
        try {
            // 1. Kiểm tra email có tồn tại không
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));
            log.info("Found user with email: {}", user.getEmail());

            // 2. Xóa token cũ nếu có
            passwordResetTokenRepository.deleteByUser(user);
            log.info("Deleted any existing tokens for user");

            // 3. Tạo mã xác nhận (6 chữ số)
            String verificationCode = String.format("%06d", new Random().nextInt(999999));

            // 4. Tạo đối tượng token
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setToken(verificationCode);
            resetToken.setUser(user);

            // Cập nhật thời gian hết hạn 15 phút sau
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 15);
            resetToken.setExpiryDate(calendar.getTime());

//            // 5. Lưu token
//            passwordResetTokenRepository.save(resetToken);
//            log.info("Saved verification code: {}", verificationCode);

            // 6. Gửi mã xác nhận qua email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(user.getEmail());
            helper.setSubject("Mã xác nhận đặt lại mật khẩu");
            helper.setText(
                    "<p>Chào " + user.getFirst_name() + " " + user.getLast_name() + ",</p>" +
                            "<p>Mã xác nhận đặt lại mật khẩu của bạn là: <b>" + verificationCode + "</b></p>" +
                            "<p>Mã này có hiệu lực trong 15 phút.</p>" +
                            "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, hãy bỏ qua email này.</p>",
                    true
            );

            mailSender.send(message);
            log.info("Verification code email sent to: {}", user.getEmail());

            return userMapper.toUserResponse(user);

        } catch (Exception e) {
            log.error("Error during password reset process: ", e);
            throw e;
        }
    }

    // ✅ Hàm kiểm tra mã xác nhận
    public boolean verifyCode(String email, String inputCode) {
        PasswordResetToken codeData = verificationCodes.get(email);
        return codeData != null &&
                codeData.getToken().equals(inputCode) &&
                codeData.getExpiryDate().after(new Date());
    }

    @Transactional
    public UserResponse resetPassword(String token, ResetPasswordRequest request) {
        log.info("Starting password reset with token: {}", token);
        try {
            // 1. Kiểm tra token có tồn tại và chưa hết hạn
            PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));
            log.info("Found valid reset token for user: {}", resetToken.getUser().getEmail());

            if (resetToken.getExpiryDate().before(new Date())) {
                passwordResetTokenRepository.delete(resetToken);
                throw new AppException(ErrorCode.TOKEN_EXPIRED);
            }

            // 2. Cập nhật mật khẩu mới
            User user = resetToken.getUser();
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            log.info("Updated password for user: {}", user.getEmail());

            // 3. Xóa token đã sử dụng
            passwordResetTokenRepository.delete(resetToken);
            log.info("Deleted used reset token");

            return userMapper.toUserResponse(user);
        } catch (Exception e) {
            log.error("Error during password reset: ", e);
            throw e;
        }
    }


    @Transactional
    public UserResponse resetPassword(String newPassword) {
        log.info("Starting password reset");
        try {
            // 1. Get current user from security context
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (user == null) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            // 2. Update password
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            log.info("Updated password for user: {}", user.getEmail());

            // 3. Delete any existing reset tokens
            passwordResetTokenRepository.deleteByUser(user);
            log.info("Deleted reset tokens for user: {}", user.getEmail());

            return userMapper.toUserResponse(user);
        } catch (Exception e) {
            log.error("Error during password reset: ", e);
            throw e;
        }
    }
}
