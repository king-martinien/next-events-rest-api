package com.kingmartinien.nextevents.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String firstname;

    private String lastname;

    private LocalDate birthDate;

    private String email;

    private String phone;

    private String city;

    private String neighbourhoods;

    private boolean accountLocked;

    private boolean enabled;

    private RoleDto role;

}
