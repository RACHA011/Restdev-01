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
public class PhotoViewDTO {

    private Long id;
    
    @NotBlank
    @Schema(description = "Photo name", example = "TSelfie", requiredMode = RequiredMode.REQUIRED)
    private String name;

    @NotBlank
    @Schema(description = "Photo description", example = "description", requiredMode = RequiredMode.REQUIRED)
    private String description;
}
