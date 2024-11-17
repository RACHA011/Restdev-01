package com.racha.restdev.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name; 

    private String description; 

    private String originalFilename; 

    @Lob
    private byte[] imageData; // Store the image data as binary data (BLOB)

    @ManyToOne
    @JoinColumn(name = "album_id", referencedColumnName = "id", nullable = false)
    private Album album; 
}
