package com.Awesome.Challenge.Online.Marketplace.API.dto;

import com.Awesome.Challenge.Online.Marketplace.API.model.Role;

import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {
        private String firstName;
        private String lastName;
        private String email;
        private Role role;
}
