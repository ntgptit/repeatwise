package com.spacedlearning.service.impl;

import com.spacedlearning.entity.User;
import com.spacedlearning.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementation of EmailService
 * Handles sending various types of emails
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from:noreply@repeatwise.com}")
    private String fromEmail;

    @Value("${app.email.verification.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public boolean sendVerificationEmail(User user, String verificationToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setFrom(fromEmail);
            message.setSubject("Xác nhận tài khoản RepeatWise");
            
            String verificationUrl = baseUrl + "/api/v1/auth/verify-email?token=" + verificationToken;
            String emailBody = buildVerificationEmailBody(user.getFullName(), verificationUrl);
            message.setText(emailBody);

            mailSender.send(message);
            log.info("Verification email sent successfully to: {}", user.getEmail());
            return true;
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}, error: {}", user.getEmail(), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendWelcomeEmail(User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setFrom(fromEmail);
            message.setSubject("Chào mừng đến với RepeatWise!");
            
            String emailBody = buildWelcomeEmailBody(user.getFullName());
            message.setText(emailBody);

            mailSender.send(message);
            log.info("Welcome email sent successfully to: {}", user.getEmail());
            return true;
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}, error: {}", user.getEmail(), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendPasswordResetEmail(User user, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setFrom(fromEmail);
            message.setSubject("Đặt lại mật khẩu RepeatWise");
            
            String resetUrl = baseUrl + "/api/v1/auth/reset-password?token=" + resetToken;
            String emailBody = buildPasswordResetEmailBody(user.getFullName(), resetUrl);
            message.setText(emailBody);

            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", user.getEmail());
            return true;
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}, error: {}", user.getEmail(), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isEmailServiceAvailable() {
        try {
            // Simple test to check if mail sender is configured
            return mailSender != null;
        } catch (Exception e) {
            log.warn("Email service is not available: {}", e.getMessage());
            return false;
        }
    }

    private String buildVerificationEmailBody(String fullName, String verificationUrl) {
        return String.format("""
            Xin chào %s,
            
            Cảm ơn bạn đã đăng ký tài khoản RepeatWise!
            
            Để hoàn tất quá trình đăng ký, vui lòng nhấp vào liên kết bên dưới để xác nhận địa chỉ email của bạn:
            
            %s
            
            Liên kết này sẽ hết hạn sau 24 giờ.
            
            Nếu bạn không tạo tài khoản này, vui lòng bỏ qua email này.
            
            Trân trọng,
            Đội ngũ RepeatWise
            """, fullName, verificationUrl);
    }

    private String buildWelcomeEmailBody(String fullName) {
        return String.format("""
            Xin chào %s,
            
            Chào mừng bạn đến với RepeatWise!
            
            Tài khoản của bạn đã được xác nhận thành công. Bây giờ bạn có thể:
            
            • Tạo các bộ học tập cá nhân
            • Sử dụng thuật toán Spaced Repetition để học hiệu quả
            • Theo dõi tiến độ học tập
            • Nhận thông báo nhắc nhở học tập
            
            Chúc bạn học tập hiệu quả!
            
            Trân trọng,
            Đội ngũ RepeatWise
            """, fullName);
    }

    private String buildPasswordResetEmailBody(String fullName, String resetUrl) {
        return String.format("""
            Xin chào %s,
            
            Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản RepeatWise.
            
            Để đặt lại mật khẩu, vui lòng nhấp vào liên kết bên dưới:
            
            %s
            
            Liên kết này sẽ hết hạn sau 1 giờ.
            
            Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.
            
            Trân trọng,
            Đội ngũ RepeatWise
            """, fullName, resetUrl);
    }
}

