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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import com.Awesome.Challenge.Online.Marketplace.API.model.Review;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/l2/reviews")
@Tag(name = "Review management")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(
            description = "Submit a new product review.",
            summary = "Endpoint for submitting a new product review.",
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
    public ResponseEntity<?> submitReview(@Valid @RequestBody ReviewDto reviewDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        } else {
            Review review = reviewService.submitReview(reviewDto);
            return new ResponseEntity<>(review, HttpStatus.CREATED);
        }
    }


    // Endpoint for retrieving reviews for a product
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable Integer productId) {
        List<Review> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(reviews);
    }

    // Endpoint for calculating average rating of a product
    @GetMapping("/product/{productId}/average-rating")
    public ResponseEntity<Double> getProductAverageRating(@PathVariable Integer productId) {
        Double averageRating = reviewService.getProductAverageRating(productId);
        return ResponseEntity.ok(averageRating);
    }
}
