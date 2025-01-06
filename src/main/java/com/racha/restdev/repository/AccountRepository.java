package com.racha.restdev.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.racha.restdev.model.Account;

public interface AccountRepository extends MongoRepository<Account, String> {
    
    Optional<Account> findTopByOrderByIdDesc();

    Optional<Account> findByEmail(String email);

    Optional<Account> findByEmailIgnoreCase(String email);

}


