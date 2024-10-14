package com.racha.restdev.payload.auth.album;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PhotoDTO {
    private Long id;

    private String name;

    private String description;

    private String fileName;

    private String download_link;

}
