package com.Awesome.Challenge.Online.Marketplace.API.controller;

import com.Awesome.Challenge.Online.Marketplace.API.exception.UnauthorizedAccessException;
import com.Awesome.Challenge.Online.Marketplace.API.model.Role;
import com.Awesome.Challenge.Online.Marketplace.API.model.User;
import com.Awesome.Challenge.Online.Marketplace.API.secuirity.auth.RegisterRequest;
import com.Awesome.Challenge.Online.Marketplace.API.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/l2/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            description = "List of all users.",
            summary = "End point to get all users information"
    )

    @GetMapping("/all_users")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/update/{userId}")
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

    @PutMapping("/updateRole/{userIdToUpdate}")
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

