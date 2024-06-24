package com.kingmartinien.nextevents.service;

import com.kingmartinien.nextevents.dto.LoginCredentialsDto;
import com.kingmartinien.nextevents.dto.LoginResponseDto;
import com.kingmartinien.nextevents.dto.ResetPasswordDto;
import com.kingmartinien.nextevents.dto.ResetPasswordRequestDto;
import com.kingmartinien.nextevents.entity.User;
import jakarta.mail.MessagingException;

public interface UserService {

    void createUser(User user) throws MessagingException;

    void activateUserAccount(String token);

    LoginResponseDto loginUser(LoginCredentialsDto loginCredentialsDto);

    void logout();

    void resetPasswordRequest(ResetPasswordRequestDto resetPasswordRequestDto) throws MessagingException;

    void resetPassword(String email, String code, ResetPasswordDto resetPasswordDto);

}
