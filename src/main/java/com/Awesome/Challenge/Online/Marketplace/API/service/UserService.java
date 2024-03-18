package com.Awesome.Challenge.Online.Marketplace.API.service;

import com.Awesome.Challenge.Online.Marketplace.API.exception.UnauthorizedAccessException;
import com.Awesome.Challenge.Online.Marketplace.API.model.Role;
import com.Awesome.Challenge.Online.Marketplace.API.model.User;
import com.Awesome.Challenge.Online.Marketplace.API.repository.UserRepository;
import com.Awesome.Challenge.Online.Marketplace.API.secuirity.auth.RegisterRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.Awesome.Challenge.Online.Marketplace.API.secuirity.config.ApplicationConfig.getCurrentUser;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Method to list all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Method to update user details
    public User updateUser( RegisterRequest userDto) {

        //get current logged user to update own account
        User user = getCurrentUser();

        // Update user details
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());

        // Check if the password is provided and update it if necessary
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        return userRepository.save(user);
    }

    // Method to check if a user is admin and update the other users role
    public void updateRoleIfAdmin( Integer userIdToUpdate, Role newRole) {
        // Retrieve current logged-in user
        User adminUser = getCurrentUser();

        // Check if that user is not admin
        if (adminUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException("Only admin users can update roles.");
        }

        // Retrieve the user to update
        User userToUpdate = userRepository.findById(userIdToUpdate)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userIdToUpdate));

        // Update the role of the user
        userToUpdate.setRole(newRole);
        userRepository.save(userToUpdate);
    }
}
