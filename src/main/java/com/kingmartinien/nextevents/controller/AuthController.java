package com.kingmartinien.nextevents.controller;

import com.kingmartinien.nextevents.dto.*;
import com.kingmartinien.nextevents.mapper.UserMapper;
import com.kingmartinien.nextevents.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@Tag(name = "Authentication")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid CreateUserDto createUserDto) throws MessagingException {
        this.userService.createUser(this.userMapper.toEntity(createUserDto));
    }

    @GetMapping("activate-account")
    @ResponseStatus(HttpStatus.OK)
    public void activateAccount(@RequestParam("code") String code) {
        this.userService.activateUserAccount(code);
    }

    @PostMapping("login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponseDto login(@RequestBody @Valid LoginCredentialsDto loginCredentialsDto) {
        return this.userService.loginUser(loginCredentialsDto);
    }

    @PostMapping("logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout() {
        this.userService.logout();
    }

    @PostMapping("reset-password-request")
    @ResponseStatus(HttpStatus.OK)
    public void resetPasswordRequest(@RequestBody @Valid ResetPasswordRequestDto resetPasswordRequestDto) throws MessagingException {
        this.userService.resetPasswordRequest(resetPasswordRequestDto);
    }

    @PostMapping("reset-password")
    @ResponseStatus(HttpStatus.OK)
    public void resetPassword(
            @RequestParam("email") String email,
            @RequestParam("code") String code,
            @RequestBody @Valid ResetPasswordDto resetPasswordDto
    ) {
        this.userService.resetPassword(email, code, resetPasswordDto);
    }

    @PostMapping("refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponseDto refreshToken(@RequestBody @Valid RefreshTokenDto refreshTokenDto) {
        return this.userService.refreshToken(refreshTokenDto);
    }

}
