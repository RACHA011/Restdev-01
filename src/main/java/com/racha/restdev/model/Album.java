package com.racha.restdev.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Document(collection = "album")
public class Album {
    @Id
    private String id;

    private String name;

    private String description;

    @DBRef
    private Account account;

}
