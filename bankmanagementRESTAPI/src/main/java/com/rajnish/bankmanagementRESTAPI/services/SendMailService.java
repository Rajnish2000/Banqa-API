package com.rajnish.bankmanagementRESTAPI.services;

import com.rajnish.bankmanagementRESTAPI.utils.GenerateOTP;
import com.rajnish.bankmanagementRESTAPI.entities.User;
import com.rajnish.bankmanagementRESTAPI.repositories.UserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SendMailService {

    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);
    private static final long MAX_VERIFICATION_ATTEMPTS = 5;

    private final JavaMailSender mailSender;
    private final StringRedisTemplate redis;
    private final UserRepository userRepository;

    public void sendOtpEmail(String toEmail) throws Exception {
        String email = toEmail.trim().toLowerCase(Locale.ROOT);
        String cooldownKey = "otp:cooldown:" + email;
        Boolean acquired = redis.opsForValue().setIfAbsent(cooldownKey, "1", RESEND_COOLDOWN);
        if (!Boolean.TRUE.equals(acquired)) {
            throw new IllegalArgumentException("An OTP was recently sent. Wait before requesting another.");
        }

        String otp = GenerateOTP.generateOtp();
        String otpKey = otpKey(email);
        redis.opsForValue().set(otpKey, otp, OTP_TTL);
        redis.delete("otp:attempts:" + email);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Your OTP Code");
            helper.setText(
                    "<p>Your one-time verification code is:</p><h1>" + otp
                            + "</h1><p>This code expires in five minutes. Do not share it.</p>",
                    true);
            mailSender.send(message);
        } catch (Exception exception) {
            redis.delete(otpKey);
            redis.delete(cooldownKey);
            throw exception;
        }
    }

    public boolean verifyOtp(String email, String inputOtp) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        String otpKey = otpKey(normalizedEmail);
        Long attempts = redis.opsForValue().increment("otp:attempts:" + normalizedEmail);
        if (Long.valueOf(1).equals(attempts)) {
            redis.expire("otp:attempts:" + normalizedEmail, OTP_TTL);
        }
        if (attempts == null || attempts > MAX_VERIFICATION_ATTEMPTS) {
            redis.delete(otpKey);
            return false;
        }

        String storedOtp = redis.opsForValue().get(otpKey);
        if (storedOtp == null || inputOtp == null) {
            return false;
        }
        boolean matches = MessageDigest.isEqual(
                storedOtp.getBytes(StandardCharsets.UTF_8),
                inputOtp.getBytes(StandardCharsets.UTF_8));
        if (matches) {
            redis.delete(otpKey);
            redis.delete("otp:attempts:" + normalizedEmail);
            User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                    .orElseThrow(() -> new IllegalArgumentException("User not found."));
            user.setEmailVerified(true);
            userRepository.save(user);
        }
        return matches;
    }

    private String otpKey(String email) {
        return "otp:code:" + email;
    }
}
