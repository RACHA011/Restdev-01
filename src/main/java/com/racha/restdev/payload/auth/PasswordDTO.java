package com.racha.restdev.payload.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PasswordDTO {

    @Size(min = 6, max = 20)
    @Schema(example = "password", description = "password", requiredMode = RequiredMode.REQUIRED, maxLength = 20, minLength = 6)
    private String password;
}
