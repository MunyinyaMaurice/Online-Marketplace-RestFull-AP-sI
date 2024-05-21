package com.Awesome.Challenge.Online.Marketplace.API.security.auth;

import com.Awesome.Challenge.Online.Marketplace.API.model.Role;
import com.Awesome.Challenge.Online.Marketplace.API.model.User;
import com.Awesome.Challenge.Online.Marketplace.API.repository.UserRepository;
import com.Awesome.Challenge.Online.Marketplace.API.security.config.JwtService;
import com.Awesome.Challenge.Online.Marketplace.API.security.token.Token;
import com.Awesome.Challenge.Online.Marketplace.API.security.token.TokenRepository;
import com.Awesome.Challenge.Online.Marketplace.API.security.token.TokenType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;

    @Autowired
    private JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public ResponseEntity<?> register(@Valid RegisterRequest request,
     BindingResult bindingResult) {
         if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
         // Check if the email already exists
         if (repo.existsByEmail(request.getEmail())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("email", "Email already exists. Please choose a different email.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
        }
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) //passwordEncoder.encode() method encodes the request.getPassword())
//                .role(request.getRole())
                .role(Role.BUYER)
                .build();

       var sevedUser = repo.save(user);
       var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

       saveUserToken(sevedUser, jwtToken);

       return ResponseEntity.ok(Map.of("accessToken", jwtToken, "refreshToken", refreshToken));
    }

    // This Method create admin in DB after checking if it not already created.
    @PostConstruct
    public void initAdminUser() {
        // Check if the admin user already exists
        var user = repo.findByEmail("admin@user.com");
        if (user.isEmpty()) {
            // Create the admin user
            var adminUser = User.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .email("admin@user.com")
                    .password(passwordEncoder.encode("Admin123@"))
                    .role(Role.ADMIN)
                    .build();
            repo.save(adminUser);
        }
    }

    public ResponseEntity<?> authenticate(@Valid AuthenticationRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

          try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            Map<String, String> errors = new HashMap<>();
            errors.put("password", "Incorrect password or email. Please try again.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
        }
      
        var user = repo.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found with email: " + request.getEmail()));
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return ResponseEntity.ok(Map.of("accessToken", jwtToken, "refreshToken", refreshToken));
    }
    // Validate email format
    // private boolean isValidEmail(String email) {
    //     // Add your email validation logic here, for example:
    //     String regex = "^(.+)@(.+)$";
    //     Pattern pattern = Pattern.compile(regex);
    //     Matcher matcher = pattern.matcher(email);
    //     return matcher.matches();
    // }

    // // Validate password format
    // private boolean isValidPassword(String password) {
    //     // Add your password validation logic here, for example:
    //     String regex = "^(?=.*[A-Z])(?=.*[!@#$%^&*()-+=]).{6,14}$";
    //     return password.matches(regex);
    // }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repo.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

}
