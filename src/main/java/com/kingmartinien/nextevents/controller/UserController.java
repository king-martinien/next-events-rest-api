package com.kingmartinien.nextevents.controller;

import com.kingmartinien.nextevents.dto.UserDto;
import com.kingmartinien.nextevents.mapper.UserMapper;
import com.kingmartinien.nextevents.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PreAuthorize("hasAuthority('PROVIDER')")
    @GetMapping
    public List<UserDto> getUsers() {
        return this.userMapper.toDto(this.userService.getAllUsers());
    }

}
