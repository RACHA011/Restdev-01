package com.racha.restdev.payload.auth.album;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PhotoPayloadDTO {

    @NotBlank
    @Schema(description = "Photo name", example = "Travel photo", requiredMode = RequiredMode.REQUIRED)
    private String name;

    @NotBlank
    @Schema(description = "Photo description", example = "Traveling", requiredMode = RequiredMode.REQUIRED)
    private String description;

}
