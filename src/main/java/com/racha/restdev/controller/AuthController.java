package com.racha.restdev.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.racha.restdev.model.Account;
import com.racha.restdev.payload.auth.AccountDTO;
import com.racha.restdev.payload.auth.AccountViewDTO;
import com.racha.restdev.payload.auth.AuthorityDTO;
import com.racha.restdev.payload.auth.PasswordDTO;
import com.racha.restdev.payload.auth.ProfileDTO;
import com.racha.restdev.payload.auth.TokenDTO;
import com.racha.restdev.payload.auth.UserLoginDTO;
import com.racha.restdev.service.AccountService;
import com.racha.restdev.service.TokenService;
import com.racha.restdev.util.constants.AccountError;
import com.racha.restdev.util.constants.AccountSuccess;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
@Tag(name = "Auth Controller", description = "Controller for Account management")
@Slf4j
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/token")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TokenDTO> token(@Valid @RequestBody UserLoginDTO userLogin) throws AuthenticationException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));
            TokenDTO token = new TokenDTO(tokenService.generateToken(authentication));
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            log.debug(AccountError.TOKEN_GENERATION_ERROR.toString() + ": " + e.getMessage());
            return new ResponseEntity<>(new TokenDTO(null), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/user/add", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Add a new user")
    @ApiResponse(responseCode = "400", description = "Please enter a valid email address and password of length between 6 and 20 characters")
    @ApiResponse(responseCode = "200", description = "Account Added successfully")
    public ResponseEntity<String> addUser(@Valid @RequestBody AccountDTO accountDTO) {
        try {
            Account account = new Account();
            account.setEmail(accountDTO.getEmail());
            account.setPassword(accountDTO.getPassword());
            accountService.save(account);

            return ResponseEntity.ok(AccountSuccess.ACCOUNT_ADDED.toString());
        } catch (Exception e) {
            log.debug(AccountError.ADD_ACCOUNT_ERROR.toString() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping(value = "/user", produces = "application/json")
    @Operation(summary = "List user api")
    @ApiResponse(responseCode = "200", description = "List of users")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token error")
    @SecurityRequirement(name = "rachadev-demo-api")
    public List<AccountViewDTO> users() {
        List<AccountViewDTO> accounts = new ArrayList<>();
        for (Account account : accountService.findall()) {
            accounts.add(new AccountViewDTO(account.getId(), account.getEmail(), account.getAuthorities()));
        }
        return accounts;
    }

    @PutMapping(value = "/users/{user_id}/update-Authorities", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Update authorities")
    @ApiResponse(responseCode = "200", description = "authority update successfully")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "400", description = "Invalid user Id")
    @ApiResponse(responseCode = "403", description = "Token error")
    @SecurityRequirement(name = "rachadev-demo-api")
    public ResponseEntity<AccountViewDTO> updateAuth(@Valid @RequestBody AuthorityDTO authorityDTO,
            @PathVariable String user_id) {
        Optional<Account> optionalAccount = accountService.findById(user_id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setAuthorities(authorityDTO.getAuthority());
            accountService.save(account);
            AccountViewDTO accountViewDTO = new AccountViewDTO(account.getId(), account.getEmail(),
                    account.getAuthorities());
            return ResponseEntity.ok(accountViewDTO);
        }
        return new ResponseEntity<AccountViewDTO>(new AccountViewDTO(), HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/profile", produces = "application/json")
    @Operation(summary = "View profile")
    @ApiResponse(responseCode = "200", description = "Profile")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token error")
    @SecurityRequirement(name = "rachadev-demo-api")
    public ProfileDTO profile(Authentication authentication) {

        String email = authentication.getName();
        // Account account = accountService.findByEmail(email).get();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();
        ProfileDTO profileDTO = new ProfileDTO(account.getId(), account.getEmail(), account.getAuthorities());
        return profileDTO;
    }

    @PutMapping(value = "/profile/update-password", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Update Password")
    @ApiResponse(responseCode = "200", description = "Password updated successfully")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token error")
    @SecurityRequirement(name = "rachadev-demo-api")
    public AccountViewDTO passwordUpdate(@Valid @RequestBody PasswordDTO passwordDTO, Authentication authentication) {

        String email = authentication.getName();
        Optional<Account> optionalAccount = accountService.findByEmail(email);
        Account account = optionalAccount.get();
        account.setPassword(passwordDTO.getPassword());
        accountService.save(account);
        return new AccountViewDTO(account.getId(), account.getEmail(), account.getAuthorities());
    }

    @DeleteMapping(value = "/profile/delete")
    @Operation(summary = "delete user profile")
    @ApiResponse(responseCode = "200", description = "user successfully deleted")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token error")
    @SecurityRequirement(name = "rachadev-demo-api")
    public ResponseEntity<String> deleteUser(Authentication authentication) {

        String email = authentication.getName();
        // Account account = accountService.findByEmail(email).get();
        Optional<Account> optionalAccount = accountService.findByEmail(email);

        if (optionalAccount.isPresent()) {
            accountService.deleteById(optionalAccount.get().getId());
            return ResponseEntity.ok("User deleted successfully");
        }

        return new ResponseEntity<String>("Bad request", HttpStatus.BAD_REQUEST);
    }
}
