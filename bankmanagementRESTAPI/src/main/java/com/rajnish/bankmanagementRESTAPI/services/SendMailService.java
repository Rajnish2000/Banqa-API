package com.rajnish.bankmanagementRESTAPI.services;

import com.rajnish.bankmanagementRESTAPI.utils.GenerateOTP;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendMailService {

    private final JavaMailSender mailSender;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @CachePut(value = "otp", key = "#toEmail")
    public String sendOtpEmail(String toEmail) throws Exception {
        System.out.println("email: "+toEmail);
        String otp = GenerateOTP.generateOtp();
        System.out.println("otp: "+otp);
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(toEmail);
//        message.setSubject("Your OTP Code verify your account");
//        message.setText("Your OTP is: " + otp + "\nIt will expire in 5 minutes.");
//        mailSender.send(message);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmail);
        helper.setSubject("Your OTP Code");
        String htmlContent =
                "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                        + "<h2 style='color: #2c3e50;'>🔐 OTP Verification</h2>"
                        + "<p>Dear User,</p>"
                        + "<p>Your One-Time Password (OTP) is:</p>"
                        + "<h1 style='color: #27ae60;'>" + otp + "</h1>"
                        + "<p>This OTP is valid for <b>5 minutes</b>.</p>"
                        + "<p style='color: red;'><b>Do not share this OTP with anyone.</b></p>"
                        + "<br>"
                        + "<p>If you did not request this, please ignore this email.</p>"
                        + "<p>Regards,<br><b>Banking Management Team</b></p>"
                        + "</div>";
        helper.setText(htmlContent, true);
        mailSender.send(message);
        return otp;
    }

    @CacheEvict(value = "otp",key = "#toEmail")
    public boolean verifyOYP(String toEmail, String inOtp) {
        String key = "otp::"+toEmail;
        String otpOut = stringRedisTemplate.opsForValue().get(key);
        //one of the another approach.
        //Object otpOut = redisTemplate.opsForValue().get(key).toString();
        System.out.println("otpOut: "+otpOut);
        return inOtp.equals(otpOut);
    }
}
