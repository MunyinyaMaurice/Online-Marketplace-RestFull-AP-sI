package com.Awesome.Challenge.Online.Marketplace.API.security.auth;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
//    @Autowired
    private final AuthenticationService service;

    @Operation(
            description = "Endpoint to create a new user.",
            summary = "Create a new user."
    )
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult bindingResult) {
        return service.register(request, bindingResult);
    }
    
    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate a user.",description = "Authenticate a user and return a JWT token.")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthenticationRequest request, BindingResult bindingResult) {
        return service.authenticate(request, bindingResult);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh a JWT token.",description = "Get Refresh a JWT token for logged in user which last for 7 days.")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

}
