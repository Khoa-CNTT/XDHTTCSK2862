package com.example.myevent_be.service;

import com.example.myevent_be.entity.UserVerificationRequest;
import com.example.myevent_be.enums.VerificationType;
import com.example.myevent_be.repository.UserVerificationRequestRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserVerificationService {
    UserVerificationRequestRepository userVerificationRequestRepository;
    JavaMailSender javaMailSender;
    int CODE_LENGTH = 6;

//    private String generateVerificationCode() {
//        Random random = new Random();
//        int code = 100000 + random.nextInt(900000); // ma 6 so
//        return String.valueOf(code);
//    }
//
//    private Date generateExpirationTime() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.MINUTE, 10); // ma co hieu luc trong 10p
//        return calendar.getTime();
//    }
//
//    @Transactional
//    public void sendVerificationCode(String email, VerificationType type, String data   ) {
//        String code = generateVerificationCode();
//        Date expirationTime = generateExpirationTime();
//
//        UserVerificationRequest request = UserVerificationRequest.builder()
//                .id(UUID.randomUUID().toString())
//                .email(email)
//                .data(data)
//                .code(code)
//                .expiration_time(expirationTime)
//                .type(type)
//                .build();
//
//        userVerificationRequestRepository.save(request);
//    }
//
//    public boolean verifyCode(String email, String code) { // kiem tra ma xac nhan
//        Optional<UserVerificationRequest> requestOpt =
//                userVerificationRequestRepository.findByEmailAndCode(email, code);
//
//        if (requestOpt.isEmpty()) {
//            throw new ResponseStatusException
//                    (HttpStatus.BAD_REQUEST, "Mã không hợp lệ hoặc đã hết hạn.");
//        }
//
//        UserVerificationRequest request = requestOpt.get();
//
//        if (request.getExpiration_time().before(new Date())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã đã hết hạn.");
//        }
//
//        return true;
//    }

    public void sendVerificationEmail(String email, String data, VerificationType type) {
        String code = UUID.randomUUID().toString().substring(0, 6); // Tạo mã xác nhận 6 ký tự
        Date expirationTime = new Date(System.currentTimeMillis() + 15 * 60 * 1000); // Hết hạn sau 15 phút

        // Lưu vào database
        UserVerificationRequest request = UserVerificationRequest.builder()
                .id(UUID.randomUUID().toString())
                .email(email)
                .code(code)
                .data(data)
                .expiration_time(expirationTime)
                .type(type)
                .build();
        userVerificationRequestRepository.save(request);

        // Gửi email
        sendEmail(email, code);
    }

    private void sendEmail(String email, String code) {
        String subject = "Xác nhận tài khoản của bạn";
        String verificationLink = "http://localhost:8080/api/auth/verify?email=" + email + "&code=" + code;

        String body = "<p>Chào bạn,</p>" +
                "<p>Vui lòng nhấn vào nút dưới đây để xác nhận tài khoản của bạn:</p>" +
                "<a href=\"" + verificationLink + "\" style=\"display:inline-block;padding:10px 20px;background:#28a745;color:#fff;text-decoration:none;border-radius:5px;\">Xác nhận tài khoản</a>" +
                "<p>Mã xác nhận của bạn: <b>" + code + "</b></p>" +
                "<p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.</p>";

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi gửi email: " + e.getMessage());
        }
    }
}
