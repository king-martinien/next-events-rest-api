package com.kingmartinien.nextevents.controller;

import com.kingmartinien.nextevents.dto.CreateUserDto;
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
    public void activateAccount(@RequestParam("token") String token) {
        this.userService.activateUserAccount(token);
    }

}
