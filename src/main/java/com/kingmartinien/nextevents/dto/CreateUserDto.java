package com.kingmartinien.nextevents.dto;

import com.kingmartinien.nextevents.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {

    @NotEmpty(message = "Firstname is required")
    @NotBlank(message = "Firstname is required")
    private String firstname;

    @NotEmpty(message = "Lastname is required")
    @NotBlank(message = "Lastname is required")
    private String lastname;

    private LocalDate birthDate;

    @NotEmpty(message = "Email is required")
    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    private String email;

    @NotEmpty(message = "Phone is required")
    @NotBlank(message = "Phone is required")
    private String phone;

    @NotEmpty(message = "City is required")
    @NotBlank(message = "City is required")
    private String city;

    @NotEmpty(message = "Neighbourhoods is required")
    @NotBlank(message = "Neighbourhoods is required")
    private String neighbourhoods;

    @NotEmpty(message = "Password is required")
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password should have at least 8 characters")
    private String password;

    private Role role;

}
