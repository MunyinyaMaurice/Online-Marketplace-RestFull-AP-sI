package com.Awesome.Challenge.Online.Marketplace.API.controller;

import com.Awesome.Challenge.Online.Marketplace.API.dto.UserDetailsDTO;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ApplicationException;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ErrorResponse;
import com.Awesome.Challenge.Online.Marketplace.API.handleValidation.HandleValidationErrors;
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
    private final HandleValidationErrors handleValidationErrors;

    @Operation(summary = "List of all users.",description = "Endpoint to get all users information. " )
    @GetMapping("/all_users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserDetailsDTO> users = userService.getAllUserDetails();
            return ResponseEntity.ok(users);
        } catch (ApplicationException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }
    }
    @Operation(summary = "Update user.",description = "Endpoint to allow user to update their information." )
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser( @RequestBody @Valid RegisterRequest userDto, BindingResult bindingResult) {

            if (bindingResult.hasErrors()) {
                return handleValidationErrors.handleValidationErrors(bindingResult);
            }
            try {
                User updatedUser = userService.updateUser(userDto);
                return ResponseEntity.ok(updatedUser);

            } catch (ApplicationException e) {
                return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                        .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
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
        } catch (ApplicationException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }
    }
}

