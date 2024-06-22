package com.kingmartinien.nextevents.service.impl;

import com.kingmartinien.nextevents.entity.Token;
import com.kingmartinien.nextevents.entity.User;
import com.kingmartinien.nextevents.enums.EmailTemplateName;
import com.kingmartinien.nextevents.exception.ConflictException;
import com.kingmartinien.nextevents.repository.TokenRepository;
import com.kingmartinien.nextevents.repository.UserRepository;
import com.kingmartinien.nextevents.service.EmailService;
import com.kingmartinien.nextevents.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${application.mailing.frontend}")
    private String confirmationUrl;

    @Override
    public void createUser(User user) throws MessagingException {
        Optional<User> foundUserWithPhone = userRepository.findByPhone(user.getPhone());
        Optional<User> foundUserWithEmail = userRepository.findByEmail(user.getEmail());
        if (foundUserWithPhone.isPresent()) {
            throw new ConflictException("User", "phone", user.getPhone());
        }
        if (foundUserWithEmail.isPresent()) {
            throw new ConflictException("User", "email", user.getEmail());
        }
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        this.userRepository.save(user);
        this.sendValidationEmail(user);
    }

    @Override
    public void activateUserAccount(String token) {
        Token savedToken = this.tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            throw new RuntimeException("Expired token");
        }

        User user = this.userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        this.userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        this.tokenRepository.save(savedToken);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        String token = this.generateAndSaveActivationToken(user);
        this.emailService.sendEmail(
                user.getEmail(),
                user.fullname(),
                "Account Activation",
                confirmationUrl,
                token,
                EmailTemplateName.ACTIVATE_ACCOUNT
        );
    }

    private String generateAndSaveActivationToken(User user) {
        Token token = Token
                .builder()
                .token(this.generateActivationCode())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        this.tokenRepository.save(token);
        return token.getToken();
    }

    private String generateActivationCode() {
        String characters = "123456789";
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * characters.length());
            codeBuilder.append(characters.charAt(index));
        }
        return codeBuilder.toString();
    }

}
