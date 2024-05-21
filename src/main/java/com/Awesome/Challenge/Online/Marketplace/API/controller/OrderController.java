package com.Awesome.Challenge.Online.Marketplace.API.controller;

import com.Awesome.Challenge.Online.Marketplace.API.dto.OrderDto;
import com.Awesome.Challenge.Online.Marketplace.API.exception.UnauthorizedAccessException;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ApplicationException;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ErrorCode;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ErrorResponse;
import com.Awesome.Challenge.Online.Marketplace.API.handleValidation.HandleValidationErrors;
import com.Awesome.Challenge.Online.Marketplace.API.model.*;
import com.Awesome.Challenge.Online.Marketplace.API.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.Awesome.Challenge.Online.Marketplace.API.security.config.ApplicationConfig.getCurrentUserId;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
@Tag(name = "Order management")
public class OrderController {

    private final OrderService orderService;
    private final HandleValidationErrors handleValidationErrors;

    @Operation(
        summary = "Submit a new order.",
            description = "Endpoint to submitting a new order.",
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
    public ResponseEntity<?> submitOrder(@RequestParam Integer productId , @Valid @RequestBody OrderDto orderDto,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return handleValidationErrors.handleValidationErrors(bindingResult);
        } try {
        // Call ProductService to create product
        Order order = orderService.submitOrder(productId,orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (ApplicationException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }
    }

    // This end point provide the list all orders associeted to product seller and list of all to admin
    @Operation(summary = "Get list of ordered products associated to your account.", description = "This end point provide the list all orders associeted to product seller and admin has that access.")
    @GetMapping("/received")
    public ResponseEntity<?> getOrdersForCurrentUser() {
        Integer userId = getCurrentUserId();
        if (userId == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED,"Your are not logged in");
        }
        try {
            List<Order> orders = orderService.getOrdersForLoggedInSeller();
            return ResponseEntity.ok(orders);
        } catch (ApplicationException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }
    }
    // This end point is for seller and admin to update placed order status
    @Operation(summary = "Update order status from PENDING to [CONFIRMED, CANCELLED].",
            description = "This end point is for seller and admin to update the status of ordered product.")

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Integer orderId, @RequestBody Map<String, String> requestBody,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return handleValidationErrors.handleValidationErrors(bindingResult);
        }try{
            Order order = orderService.updateOrderStatus(orderId, requestBody);
            return ResponseEntity.ok(order);
        }catch (ApplicationException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }
    }
}

