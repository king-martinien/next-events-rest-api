package com.kingmartinien.nextevents.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDto {

    @NotEmpty(message = "refresh token is required")
    @NotBlank(message = "refresh token is required")
    private String refreshToken;

}
