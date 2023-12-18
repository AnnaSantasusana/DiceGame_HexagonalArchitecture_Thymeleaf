package com.itacademy.dicegame.domain.models.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotBlank(message = "The email cannot be blank")
    @Email(message = "The email format is invalid")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    private String password;
}
