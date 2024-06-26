package com.kingmartinien.nextevents.service.impl;

import com.kingmartinien.nextevents.dto.*;
import com.kingmartinien.nextevents.entity.Activation;
import com.kingmartinien.nextevents.entity.Role;
import com.kingmartinien.nextevents.entity.Token;
import com.kingmartinien.nextevents.entity.User;
import com.kingmartinien.nextevents.enums.EmailTemplateName;
import com.kingmartinien.nextevents.exception.ConflictException;
import com.kingmartinien.nextevents.exception.ResourceNotFoundException;
import com.kingmartinien.nextevents.repository.ActivationRepository;
import com.kingmartinien.nextevents.repository.RoleRepository;
import com.kingmartinien.nextevents.repository.TokenRepository;
import com.kingmartinien.nextevents.repository.UserRepository;
import com.kingmartinien.nextevents.security.JwtService;
import com.kingmartinien.nextevents.service.EmailService;
import com.kingmartinien.nextevents.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ActivationRepository activationRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;

    @Value("${application.mailing.frontend.activateAccount}")
    private String confirmationUrl;
    @Value("${application.mailing.frontend.resetPassword}")
    private String resetPasswordUrl;

    @Override
    public void createUser(User user) throws MessagingException {
        Optional<User> foundUserWithPhone = userRepository.findByPhone(user.getPhone());
        Optional<User> foundUserWithEmail = userRepository.findByEmail(user.getEmail());
        Optional<Role> optionalRole = this.roleRepository.findByLabel(user.getRole().getLabel());
        if (foundUserWithPhone.isPresent()) {
            throw new ConflictException("User", "phone", user.getPhone());
        }
        if (foundUserWithEmail.isPresent()) {
            throw new ConflictException("User", "email", user.getEmail());
        }
        if (optionalRole.isEmpty()) {
            throw new ResourceNotFoundException("Role", "label", user.getRole().getLabel().name());
        }
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        user.setRole(optionalRole.get());
        this.userRepository.save(user);
        this.sendValidationEmail(user);
    }

    @Override
    public void activateUserAccount(String code) {
        Activation savedActivation = this.activationRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid code"));

        if (LocalDateTime.now().isAfter(savedActivation.getExpiresAt())) {
            throw new RuntimeException("Expired code");
        }

        User user = this.userRepository.findById(savedActivation.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        this.userRepository.save(user);
        savedActivation.setValidatedAt(LocalDateTime.now());
        this.activationRepository.save(savedActivation);
    }

    @Override
    public LoginResponseDto loginUser(LoginCredentialsDto loginCredentialsDto) {
        Authentication authentication = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginCredentialsDto.getEmail(), loginCredentialsDto.getPassword())
        );
        if (authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            HashMap<String, Object> claims = new HashMap<>();
            claims.put("fullname", user.fullname());
            String jwtToken = this.jwtService.generateJwtToken(claims, user);
            String refreshToken = this.jwtService.generateRefreshToken(user);
            this.revokeAllUSerTokens(user);
            this.saveUserToken(jwtToken, refreshToken, user);
            return LoginResponseDto.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
        }
        return null;
    }

    @Override
    public void logout() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        revokeAllUSerTokens(user);
    }

    @Override
    public void resetPasswordRequest(ResetPasswordRequestDto resetPasswordRequestDto) throws MessagingException {
        User user = this.userRepository.findByEmail(resetPasswordRequestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", resetPasswordRequestDto.getEmail()));
        this.sendResetPasswordEmail(user);
    }

    @Override
    public void resetPassword(String email, String code, ResetPasswordDto resetPasswordDto) {
        Activation activation = this.activationRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Invalid code"));
        User user = this.userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (activation.getUser().getEmail().equals(user.getEmail())) {
            user.setPassword(this.passwordEncoder.encode(resetPasswordDto.getPassword()));
            this.userRepository.save(user);
            activation.setValidatedAt(LocalDateTime.now());
            this.activationRepository.save(activation);
        }

    }

    @Override
    public LoginResponseDto refreshToken(RefreshTokenDto refreshTokenDto) {
        Token token = this.tokenRepository.findByRefreshToken(refreshTokenDto.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (!this.jwtService.isRefreshTokenValid(token.getRefreshToken(), token.getUser())) {
            throw new RuntimeException("Expired refresh token");
        }
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("fullname", token.getUser().fullname());
        String newToken = this.jwtService.generateJwtToken(claims, token.getUser());
        String newRefreshToken = this.jwtService.generateRefreshToken(token.getUser());
        this.revokeAllUSerTokens(token.getUser());
        this.saveUserToken(newToken, newRefreshToken, token.getUser());
        return LoginResponseDto.builder().accessToken(newToken).refreshToken(newRefreshToken).build();
    }

    @Override
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    private void revokeAllUSerTokens(User user) {
        List<Token> validTokens = this.tokenRepository.findAllValidTokensByUser(user.getId());
        validTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        this.tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String jwtToken, String refreshToken, User user) {
        Token token = Token.builder()
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .refreshToken(refreshToken)
                .user(user)
                .build();
        this.tokenRepository.save(token);
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

    private void sendResetPasswordEmail(User user) throws MessagingException {
        String token = this.generateAndSaveActivationToken(user);
        this.emailService.sendEmail(
                user.getEmail(),
                user.fullname(),
                "Reset Password",
                resetPasswordUrl,
                token,
                EmailTemplateName.RESET_PASSWORD_REQUEST
        );
    }

    private String generateAndSaveActivationToken(User user) {
        Activation activation = Activation
                .builder()
                .code(this.generateActivationCode())
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        this.activationRepository.save(activation);
        return activation.getCode();
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
