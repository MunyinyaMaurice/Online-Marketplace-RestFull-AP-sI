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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.Awesome.Challenge.Online.Marketplace.API.secuirity.config.ApplicationConfig.getCurrentUserId;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/l2/order")
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
    @PostMapping("/press_order")
    public ResponseEntity<?> submitOrder(@Valid @RequestBody OrderDto orderDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        } else {
            try {
                ResponseEntity<?> orderResponse = orderService.pressOrder(orderDto);
                return new ResponseEntity<>(orderResponse.getBody(), HttpStatus.CREATED);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        }
    }

    // This end point provide the list all orders associeted to product seller and list of all to admin
    @GetMapping("/orders_list")
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
    @PutMapping("/update/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Integer orderId, @RequestBody Map<String, String> requestBody) {
        try {
            if (orderId == null) {
                return ResponseEntity.badRequest().body("Order ID cannot be null.");
            }

            String orderStatus = requestBody.get("orderStatus");

            // Convert the orderStatus string to OrderStatus enum
            OrderStatus newStatus = OrderStatus.valueOf(orderStatus);

            // Update the order status
            Order updatedOrder = orderService.updateOrderStatus(orderId, newStatus);

            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid order status value.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update order status.");
        }
    }

}
