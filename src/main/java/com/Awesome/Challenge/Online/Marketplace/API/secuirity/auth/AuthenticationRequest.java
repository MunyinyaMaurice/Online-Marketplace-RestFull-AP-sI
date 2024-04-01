package com.Awesome.Challenge.Online.Marketplace.API.secuirity.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @Valid
    @NotBlank
    @Email
    private String email;
     @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()-+=]).{6,14}$",
            message = "Password must be 6 to 14 characters long and contain at least one uppercase letter and one special character.")
    private String password;
}
