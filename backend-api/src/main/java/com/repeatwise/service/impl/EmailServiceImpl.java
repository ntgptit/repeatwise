package com.repeatwise.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.repeatwise.service.IEmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.repeatwise.log.LogEvent;

/**
 * Email Service Implementation - Sends emails via SMTP
 *
 * Requirements:
 * - UC-024: Manage Notifications
 * - Spring Mail integration
 * - Thymeleaf template engine for HTML emails
 *
 * Configuration:
 * - SMTP settings in application.yml
 * - Email templates in resources/templates/email/
 *
 * @author RepeatWise Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    private static final String FROM_EMAIL = "noreply@repeatwise.com";
    private static final String FROM_NAME = "RepeatWise";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @Async
    @Override
    public boolean sendDailyReminderEmail(
        final String recipientEmail,
        final String recipientName,
        final int dueCardsCount,
        final int streakDays
    ) {
        log.info("event={} Sending daily reminder email to: {}", LogEvent.START, recipientEmail);

        if (!isValidEmail(recipientEmail)) {
            log.error("event={} Invalid email address: {}", LogEvent.EX_VALIDATION, recipientEmail);
            return false;
        }

        final Map<String, Object> variables = new HashMap<>();
        variables.put("recipientName", recipientName);
        variables.put("dueCardsCount", dueCardsCount);
        variables.put("streakDays", streakDays);
        variables.put("reviewUrl", "https://app.repeatwise.com/review");

        final String subject = String.format(
            "📚 [RepeatWise] You have %d card%s due for review",
            dueCardsCount,
            dueCardsCount > 1 ? "s" : ""
        );

        return sendTemplatedEmail(
            recipientEmail,
            subject,
            "email/daily-reminder",
            variables
        );
    }

    @Async
    @Override
    public boolean sendTestNotificationEmail(
        final String recipientEmail,
        final String recipientName,
        final int dueCardsCount
    ) {
        log.info("event={} Sending test notification email to: {}", LogEvent.NOTIF_TEST_SEND, recipientEmail);

        if (!isValidEmail(recipientEmail)) {
            log.error("event={} Invalid email address: {}", LogEvent.EX_VALIDATION, recipientEmail);
            return false;
        }

        final Map<String, Object> variables = new HashMap<>();
        variables.put("recipientName", recipientName);
        variables.put("dueCardsCount", dueCardsCount);

        final String subject = "[RepeatWise] Test Notification";

        return sendTemplatedEmail(
            recipientEmail,
            subject,
            "email/test-notification",
            variables
        );
    }

    @Override
    public boolean sendTemplatedEmail(
        final String recipientEmail,
        final String subject,
        final String templateName,
        final Map<String, Object> templateVariables
    ) {
        try {
            final MimeMessage message = mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(FROM_EMAIL, FROM_NAME);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);

            final Context context = new Context();
            context.setVariables(templateVariables);

            final String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("event={} Email sent successfully to: {}", LogEvent.SUCCESS, recipientEmail);
            return true;

        } catch (MessagingException e) {
            log.error("event={} Failed to create email message for: {}", LogEvent.FAIL, recipientEmail, e);
            return false;
        } catch (MailException e) {
            log.error("event={} Failed to send email to: {}", LogEvent.FAIL, recipientEmail, e);
            return false;
        } catch (Exception e) {
            log.error("event={} Unexpected error sending email to: {}", LogEvent.FAIL, recipientEmail, e);
            return false;
        }
    }

    @Override
    public boolean sendPlainTextEmail(
        final String recipientEmail,
        final String subject,
        final String body
    ) {
        try {
            final SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(recipientEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            log.info("event={} Plain text email sent successfully to: {}", LogEvent.SUCCESS, recipientEmail);
            return true;

        } catch (MailException e) {
            log.error("event={} Failed to send plain text email to: {}", LogEvent.FAIL, recipientEmail, e);
            return false;
        }
    }

    @Override
    public boolean isValidEmail(final String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
