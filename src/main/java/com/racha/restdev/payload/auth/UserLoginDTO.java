package com.racha.restdev.payload.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserLoginDTO {

    @Email
    @Schema(example = "user@user.com", description = "Email address", requiredMode = RequiredMode.REQUIRED)
    private String email;

    @Size(min = 6, max = 20)
    @Schema(example = "password", 
    description = "password", 
    requiredMode = RequiredMode.REQUIRED, 
    maxLength = 20, 
    minLength = 6)
    private String password;
} 