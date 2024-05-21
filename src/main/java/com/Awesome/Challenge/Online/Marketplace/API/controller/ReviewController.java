package com.Awesome.Challenge.Online.Marketplace.API.controller;

import com.Awesome.Challenge.Online.Marketplace.API.dto.ReviewDto;
import com.Awesome.Challenge.Online.Marketplace.API.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import com.Awesome.Challenge.Online.Marketplace.API.model.Review;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@Tag(name = "Review management")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(
        summary = "Submit a new product review.",
            description = "Endpoint for submitting a new product review.",        
            responses = {
                    @ApiResponse(
                            description = "Review submitted successfully.",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )

    // Endpoint for submitting a review
    @PostMapping("/submitReview")
    public ResponseEntity<Map<String, Object>> submitReview(@RequestParam Integer productId, @Valid @RequestBody ReviewDto reviewDto, BindingResult bindingResult) {
       if (bindingResult.hasErrors()) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        response.put("errors", errors);
        response.put("message", "Validation failed");
        return ResponseEntity.badRequest().body(response); // Return a 400 status code for validation errors
    } else {
        ResponseEntity<Map<String, Object>> responseEntity= reviewService.submitReview(productId,reviewDto,bindingResult);
            return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
        }
    }


    // Endpoint for retrieving reviews for a product
    @Operation(summary = "Get reviews for a product.", description = "Endpoint for retrieving reviews for a product.")
    @GetMapping("/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable Integer productId) {
        List<Review> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(reviews);
    }

    // Endpoint for calculating average rating of a product
    @Operation(summary = "Get average rating for a product.", description = "Endpoint for calculating average rating of a product.")
    @GetMapping("/average-rating/{productId}")
    public ResponseEntity<Double> getProductAverageRating(@PathVariable Integer productId) {
        Double averageRating = reviewService.getProductAverageRating(productId);
        return ResponseEntity.ok(averageRating);
    }
}
