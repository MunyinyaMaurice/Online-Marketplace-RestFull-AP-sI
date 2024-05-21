package com.Awesome.Challenge.Online.Marketplace.API.service;

import com.Awesome.Challenge.Online.Marketplace.API.dto.UserDetailsDTO;
import com.Awesome.Challenge.Online.Marketplace.API.exception.UnauthorizedAccessException;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ApplicationException;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ErrorCode;
import com.Awesome.Challenge.Online.Marketplace.API.model.Role;
import com.Awesome.Challenge.Online.Marketplace.API.model.User;
import com.Awesome.Challenge.Online.Marketplace.API.repository.UserRepository;
import com.Awesome.Challenge.Online.Marketplace.API.security.auth.RegisterRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.Awesome.Challenge.Online.Marketplace.API.security.config.ApplicationConfig.getCurrentUser;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Method to list all users
    public List<UserDetailsDTO> getAllUserDetails() {

        return userRepository.getAllUserDetails();
    }
    // Method to update user details
    public User updateUser( RegisterRequest userDto) {

        //get current logged currentUser to update own account
        try {
        User currentUser = getCurrentUser();
        if (currentUser ==null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED);
        }
            if (userDto.getFirstName() != null) {
                if (!userRepository.existsByName(userDto.getFirstName())) {
                    currentUser.setFirstName(userDto.getFirstName());
                } else {
                    throw new ApplicationException(ErrorCode.CONFLICT);
                }
            }
            if (userDto.getLastName() != null) {
                if (!userRepository.existsByName(userDto.getLastName())) {
                    currentUser.setLastName(userDto.getLastName());
                } else {
                    throw new ApplicationException(ErrorCode.CONFLICT);
                }
            }
            if (userDto.getEmail() != null) {
                if (!userRepository.existsByEmail(userDto.getEmail())) {
                    currentUser.setEmail(userDto.getEmail());
                } else {
                    throw new ApplicationException(ErrorCode.CONFLICT);
                }
            }

                // Check if the password is provided and update it if necessary
                if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                    currentUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
                }

                return userRepository.save(currentUser);

        }catch (ApplicationException e){
        throw new ApplicationException(ErrorCode.SERVER_ERROR);
        }
    }


    // Method to check if a user is admin and update the other users role
    public void updateRoleIfAdmin( Integer userIdToUpdate, Role newRole) {
        try {
            // Retrieve current logged-in user
            User adminUser = getCurrentUser();
            if (adminUser == null) {
                throw new ApplicationException(ErrorCode.UNAUTHORIZED);
            }
            // Check if that user is not admin
            if (adminUser.getRole() != Role.ADMIN) {
                throw new ApplicationException(ErrorCode.UNAUTHORIZED, "Only admin users can update roles.");
            }

            // Retrieve the user to update
            User userToUpdate = userRepository.findById(userIdToUpdate)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));

            // Update the role of the user
            userToUpdate.setRole(newRole);
            userRepository.save(userToUpdate);
        }catch (ApplicationException e) {
            throw new ApplicationException(ErrorCode.SERVER_ERROR);
        }
    }
}
