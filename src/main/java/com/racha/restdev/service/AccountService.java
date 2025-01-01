package com.racha.restdev.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.racha.restdev.model.Account;
import com.racha.restdev.repository.AccountRepository;
import com.racha.restdev.util.constants.Authority;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Account save(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        if (account.getAuthorities() == null) {
            account.setAuthorities(Authority.USER.toString());
        }
        if (account.getId() == null || account.getId().isEmpty()) {
            // Check if the email already exists
            if (accountRepository.findByEmailIgnoreCase(account.getEmail()).isPresent()) {
                throw new RuntimeException("Email already exists");
            }
            Optional<Account> maxIdOpt = findMaxId();
            String newidString = maxIdOpt.map(Account::getId).orElse("0");
            Long newId = Long.parseLong(newidString) + 1;
            account.setId(String.valueOf(newId));
        }
        return accountRepository.save(account);
    }

    public Optional<Account> findMaxId() {
        return accountRepository.findTopByOrderByIdDesc();
    }

    public List<Account> findall() {
        return accountRepository.findAll();
    }

    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public void deleteById(String id) {
        accountRepository.deleteById(id);
    }

    public Optional<Account> findById(String id) {
        return accountRepository.findById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountRepository.findByEmail(email);
        if (!optionalAccount.isPresent()) {
            throw new UsernameNotFoundException("Account not found");
        }
        Account account = optionalAccount.get();

        List<GrantedAuthority> grantedAuthority = new ArrayList<>();
        grantedAuthority.add(new SimpleGrantedAuthority(account.getAuthorities()));

        return new User(account.getEmail(), account.getPassword(), grantedAuthority);
    }
}
