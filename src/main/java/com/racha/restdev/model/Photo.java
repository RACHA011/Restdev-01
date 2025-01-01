package com.racha.restdev.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "photo")
public class Photo {
    @Id
    private String id;

    private String name;

    private String description;

    private String originalFilename;

    private byte[] imageData; // Store the image data as binary data (BLOB)

    @DBRef
    private Album album;
}
