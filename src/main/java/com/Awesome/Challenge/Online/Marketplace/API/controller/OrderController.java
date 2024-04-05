package com.Awesome.Challenge.Online.Marketplace.API.controller;

import com.Awesome.Challenge.Online.Marketplace.API.dto.OrderDto;
import com.Awesome.Challenge.Online.Marketplace.API.exception.UnauthorizedAccessException;
import com.Awesome.Challenge.Online.Marketplace.API.model.*;
import com.Awesome.Challenge.Online.Marketplace.API.service.OrderService;
import io.jsonwebtoken.lang.Collections;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.Awesome.Challenge.Online.Marketplace.API.secuirity.config.ApplicationConfig.getCurrentUserId;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/order")
@Tag(name = "Order management")
public class OrderController {

    private final OrderService orderService;

    @Operation(
            description = "Endpoint to submitting a new order.",
            summary = "Submit a new order.",
            responses = {
                    @ApiResponse(
                            description = "order submitted successfully.",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )

//This end point help any user who is logged in to order any product listed
    @PostMapping("/submit_order")
    public ResponseEntity<?> submitOrder(@RequestParam Integer productId , @Valid @RequestBody OrderDto orderDto, BindingResult bindingResult) {
            if (bindingResult.hasErrors()) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        response.put("errors", errors);
        response.put("message", "Validation failed");
        return ResponseEntity.badRequest().body(response); // Return a 400 status code for validation errors
    } else {
        // Call ProductService to create product
        ResponseEntity<?> responseEntity = orderService.placeOrder(productId,orderDto, bindingResult);
        return responseEntity;
        }   
    }

    // This end point provide the list all orders associeted to product seller and list of all to admin
    @GetMapping("/received")
    public ResponseEntity<?> getOrdersForCurrentUser() {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        try {
            List<Order> orders = orderService.getOrdersForLoggedInSeller();
            return ResponseEntity.ok(orders);
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    // This end point is for seller and admin to update placed order status
    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Integer orderId, @RequestBody Map<String, String> requestBody, BindingResult bindingResult) {
        
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = bindingResult.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                return ResponseEntity.badRequest().body(errors);
            }
            if (bindingResult.hasErrors()) {
                Map<String, Object> response = new HashMap<>();
                Map<String, String> errors = bindingResult.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                response.put("errors", errors);
                response.put("message", "Validation failed");
                return ResponseEntity.badRequest().body(response); // Return a 400 status code for validation errors
            } else {
                ResponseEntity<?> responseEntity = orderService.updateOrderStatus(orderId, requestBody, bindingResult);
                return responseEntity;
       
    }
}

}
