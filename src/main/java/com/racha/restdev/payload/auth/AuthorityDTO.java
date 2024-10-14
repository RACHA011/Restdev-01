package com.racha.restdev.payload.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AuthorityDTO {
    @NotBlank
    @Schema(example = "USER",
     description = "Authorities", requiredMode = RequiredMode.REQUIRED)
    private String authority;
}
