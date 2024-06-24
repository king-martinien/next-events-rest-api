package com.kingmartinien.nextevents.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordDto {

    @NotEmpty(message = "The password is required")
    @NotBlank(message = "The password is required")
    @Size(min = 8, message = "Password should have at least 8 characters")
    private String password;

}
