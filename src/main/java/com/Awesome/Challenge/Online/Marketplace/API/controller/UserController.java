package com.Awesome.Challenge.Online.Marketplace.API.controller;

import com.Awesome.Challenge.Online.Marketplace.API.dto.UserDetailsDTO;
import com.Awesome.Challenge.Online.Marketplace.API.exception.UnauthorizedAccessException;
import com.Awesome.Challenge.Online.Marketplace.API.model.Role;
import com.Awesome.Challenge.Online.Marketplace.API.model.User;
import com.Awesome.Challenge.Online.Marketplace.API.security.auth.RegisterRequest;
import com.Awesome.Challenge.Online.Marketplace.API.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "List of all users.",description = "Endpoint to get all users information. " )
    @GetMapping("/all_users")
    public List<UserDetailsDTO> getAllUsers() {
        try {
            List<UserDetailsDTO> users = userService.getAllUserDetails();
            return ResponseEntity.ok(users).getBody();
        } catch (Exception e) {
            return List.of();
        }
    }
    @Operation(summary = "Update user.",description = "Endpoint to allow user to update their information." )
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser( @RequestBody @Valid RegisterRequest userDto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(errors); // Return a list of error messages
            } else {

                User updatedUser = userService.updateUser( userDto);
                return ResponseEntity.ok(updatedUser);
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user.");
        }
    }
    @Operation(summary = "Update user role.",description = "Endpoint to allow admin to update user role. " )
    @PutMapping("/{userIdToUpdateRole}")
    public ResponseEntity<?> updateRoleIfAdmin( @PathVariable Integer userIdToUpdate,  @RequestBody Map<String, String> requestBody) {
        try {
            String newRoles = requestBody.get("role");

            // Convert the orderStatus string to OrderStatus enum
            Role newRole = Role.valueOf(newRoles);

            userService.updateRoleIfAdmin(userIdToUpdate, newRole);
            return ResponseEntity.ok("User role updated successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update user role.");
        }
    }
}

