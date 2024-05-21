package com.Awesome.Challenge.Online.Marketplace.API.controller;

import com.Awesome.Challenge.Online.Marketplace.API.dto.ReviewDto;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ApplicationException;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ErrorResponse;
import com.Awesome.Challenge.Online.Marketplace.API.handleValidation.HandleValidationErrors;
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
    private final HandleValidationErrors handleValidationErrors;

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
    @PostMapping
    public ResponseEntity<?> submitReview(@RequestParam Integer productId, @Valid @RequestBody ReviewDto reviewDto, BindingResult bindingResult) {
      if(bindingResult.hasErrors()){
          return handleValidationErrors.handleValidationErrors(bindingResult);
      }
        try {
        Review review= reviewService.submitReview(productId,reviewDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } catch (ApplicationException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }
    }
    // Endpoint for retrieving reviews for a product
    @Operation(summary = "Get reviews for a product.", description = "Endpoint for retrieving reviews for a product.")
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductReviews(@PathVariable Integer productId) {
        try {
            List<Review> reviews = reviewService.getProductReviews(productId);
            return ResponseEntity.ok(reviews);
        }catch (ApplicationException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }
    }

    // Endpoint for calculating average rating of a product
    @Operation(summary = "Get average rating for a product.", description = "Endpoint for calculating average rating of a product.")
    @GetMapping("/average-rating/{productId}")
    public ResponseEntity<?> getProductAverageRating(@PathVariable Integer productId) {
        try {
            Double averageRating = reviewService.getProductAverageRating(productId);
            return ResponseEntity.ok(averageRating);
        }catch (ApplicationException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }
    }
}
