package com.edusphere.service;

import com.edusphere.entity.PasswordResetToken;
import com.edusphere.entity.User;
import com.edusphere.repository.PasswordResetTokenRepository;
import com.edusphere.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;
    public PasswordResetService(PasswordResetTokenRepository passwordResetTokenRepository,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                JavaMailSender mailSender) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    public PasswordResetToken requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No account found with email " + email));

        PasswordResetToken token = new PasswordResetToken(user);
        passwordResetTokenRepository.save(token);

        sendResetEmail(user.getEmail(), token.getToken());

        return token;
    }

    private void sendResetEmail(String toEmail, String token) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reset your EduSphere password");
        message.setText(
                "Click the link below to reset your password. This link expires in 60 minutes.\n\n" + resetLink
        );

        mailSender.send(message);
    }

    public void resetPassword(String token, String newRawPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));

        resetToken.markUsed();
        passwordResetTokenRepository.save(resetToken);

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);
    }
}