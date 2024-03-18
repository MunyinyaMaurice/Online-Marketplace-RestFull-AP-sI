package com.Awesome.Challenge.Online.Marketplace.API.secuirity.auth;

import com.Awesome.Challenge.Online.Marketplace.API.model.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @Valid
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private String email;
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()-+=]).{6,14}$",
            message = "Password must be 6 to 14 characters long and contain at least one uppercase letter and one special character.")
    @NotNull
    private String password;

    private Role role;
}