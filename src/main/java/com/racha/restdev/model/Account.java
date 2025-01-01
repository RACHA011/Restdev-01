package com.racha.restdev.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Document(collection = "account")
public class Account {
    @Id
    private String id;

    private String email;

    private String password;

    private String authorities;
}
