package com.edusphere.service;

import com.edusphere.entity.PasswordResetToken;
import com.edusphere.entity.User;
import com.edusphere.repository.PasswordResetTokenRepository;
import com.edusphere.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(PasswordResetTokenRepository passwordResetTokenRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public PasswordResetToken requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No account found with email " + email));

        PasswordResetToken token = new PasswordResetToken(user);
        passwordResetTokenRepository.save(token);

        // TODO: send an email to user.getEmail() containing a link with token.getToken(),
        // once a mail-sending component exists (security/notifications phase)

        return token;
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

